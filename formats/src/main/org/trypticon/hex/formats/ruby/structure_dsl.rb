
include_class org.trypticon.hex.anno.SimpleMutableAnnotation
include_class org.trypticon.hex.anno.SimpleMutableGroupAnnotation
include_class org.trypticon.hex.interpreters.FixedLengthInterpreter

#
# Holds information for a single sequence of annotations being dropped into place.
#
class DropContext
  attr_reader :annotations

  def initialize(annotations)
    @annotations = annotations
  end

  # call-seq:
  #   get_int_value(:length, 2)       #=> an Integer
  #   get_int_value(:length, :length) #=> an Integer
  #
  # Gets an integer value from a previously encountered annotation.
  #
  # Parameters:
  #   desc - symbol describing the parameter (used primarily for exception messages)
  #   param - the parameter provided:
  #         If it's a number, the number is returned after converting to an integer.
  #         If it's a string or symbol, the number is taken from the field with that name, which must
  #           have occurred previous to the current field being processed.
  #   binary - the binary to look up values in if needed
  #
  def get_int_value(desc, param, binary)
    if param.is_a?(Numeric)
      param.to_i
    elsif param.is_a?(Symbol) || param.is_a?(String)
      annotation = @annotations.find { |a| a.note == param.to_s }
      if !annotation
        raise "No annotation called #{param} to get the length from"
      end
      annotation.interpret(binary).int_value
    else
      raise "No way to determine the #{desc}"
    end
  end
end

#
# A simple structure holds an interpreter and its length.
#
class SimpleStructure
  attr_reader :name

  # Parameters:
  #   name             - the name the annotation will receive
  #   interpreter_map  - specifies the interpreter to use to interpret the binary at the location of the
  #                      annotation.  Supported interpreter map options:
  #     :name     - the short name of the interpreter
  #     :length   - the length of the structure:
  #         If it's a number, the number is taken as the length (after converting to an integer.)
  #         If it's a string or symbol, the number is taken from the field with that name, which must
  #           have occurred previous to the current field being processed.
  #     Plus various options depending on the interpreter used.
  #
  def initialize(name, interpreter_map)
    @name = name

    @length = interpreter_map.delete(:length)

    # Converts the keys to string in the process.  Symbols look better for hash keys on the Ruby side,
    # but the Java side expects a map keyed by String.
    tmp = {}
    interpreter_map.each_pair do |key, value|
      tmp[key.to_s] = value.to_s
    end

    @interpreter = $interpreter_storage.from_map(tmp)
    if !@interpreter
      raise "Interpreter not found: #{interpreter_map.inspect}"
    end
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)
    length = @interpreter.is_a?(FixedLengthInterpreter) ?
             @interpreter.value_length :
             drop_context.get_int_value(:length, @length, binary)

    SimpleMutableAnnotation.new(position, length, @interpreter, @name)
  end
end

#
# An array structure will create a sequence of the same structure.
#
class ArrayStructure
  attr_reader :name

  def initialize(name, start_index, size, element_structure)
    @name = name
    @start_index = start_index
    @size = size
    @element_structure = element_structure
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)

    annotations = []
    pos = position
    size = drop_context.get_int_value(:size, @size, binary)

    child_drop_context = DropContext.new(annotations)

    (@start_index...@start_index+size).each do |i|

      annotation = @element_structure.do_drop(child_drop_context, binary, pos)
      annotation.note = "#{self.name}[#{i}]"

      annotations << annotation

      pos += annotation.length
    end

    length = pos - position
    SimpleMutableGroupAnnotation.new(position, length, self.name.to_s, annotations)
  end
end

#
# Main DSL class.
#
class StructureDSL
  attr_reader :name
  attr_reader :fields

  def initialize(name)
    @name = name
    @fields = []
  end

  # Catch-all allows simple interpreter-based fields to be supported without creating methods for them all.
  def method_missing(method, *args)
    # name of the method call becomes the interpreter name
    # first argument becomes the field name
    # the tail goes into the options for the field

    StructureDSL.send(:define_method, method) do |name, *args1|
      interpreter_map = args1.shift || {}
      interpreter_map_with_name = interpreter_map.merge(:name => method)
      @fields << SimpleStructure.new(name, interpreter_map_with_name)
    end

    send(method, *args)
  end

  # Creates an array structure.
  #
  #   name     - the name the array structure will receive when dropped into an annotation set.  The elements
  #              will be named after the array structure itself.
  #   options  - a map of options.  Supported options:
  #     :size          - the size of the array, i.e. the number of elements.
  #     :element_type  - the type of each element in the array
  #     :start_index   - the start index (default: 0)
  #
  def array(name, options = {})
    size         = options[:size]         || raise("size option not provided")
    element_type = options[:element_type] || raise("element_type option not provided")
    start_index  = options[:start_index]  || 0

    # TODO: Support complex structures inside the array too.
    # Name not needed here because ArrayStructure stamps its own name onto the elements.
    element_structure = SimpleStructure.new(nil, { :name => element_type })

    @fields << ArrayStructure.new(name, start_index, size, element_structure)
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)

    annotations = []
    pos = position

    drop_context = DropContext.new(annotations)

    @fields.each do |field|
      annotation = field.do_drop(drop_context, binary, pos)

      annotations << annotation

      pos += annotation.length
    end

    length = pos - position
    SimpleMutableGroupAnnotation.new(position, length, self.name.to_s, annotations)
  end

  # Implements Structure
  def drop(binary, position)
    do_drop(DropContext.new([]), binary, position)
  end

end

# Main entry point, a simple method which instantiates the DSL and evaluates the caller's block
# against the DSL.
#
# Parameter:
#   name - the name of the structure.
#
def structure(name, &block)
  structure = StructureDSL.new(name)

  structure.instance_eval(&block)

  structure
end
