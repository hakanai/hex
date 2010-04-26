
class Field
  attr_reader :name
  attr_reader :interpreter_map
  attr_reader :length

  def initialize(name, interpreter_map = {})
    @name = name.to_s

    @length = interpreter_map.delete(:length)

    # Converts the keys to string in the process.  Symbols look better for hash keys on the Ruby side,
    # but the Java side expects a map keyed by String.
    @interpreter_map = {}
    interpreter_map.each_pair do |key, value|
      @interpreter_map[key.to_s] = value.to_s
    end
  end
end

class StructureDSL
  attr_accessor :name
  attr_accessor :fields

  def initialize(name)
    @name = name
    @fields = []
  end

  def method_missing(method, *args)
    # name of the method call becomes the interpreter name
    # first argument becomes the field name
    # the tail goes into the options for the field

    StructureDSL.send(:define_method, method) do |name, *args1|
      (interpreter_map, rest) = args1
      interpreter_map ||= {}
      interpreter_map_with_name = interpreter_map.merge(:name => method)
      @fields << Field.new(name, interpreter_map_with_name)
    end

    send(method, *args)
  end

  # Implements Structure
  def drop(binary, position)

    annotations = []
    pos = position

    @fields.each do |field|
      interpreter = $interpreter_storage.from_map(field.interpreter_map)
      if !interpreter
        raise "Interpreter not found: #{field.interpreter_map.inspect}"
      end

      length = -1
      if interpreter.is_a?(org.trypticon.hex.interpreters.FixedLengthInterpreter)
        length = interpreter.value_length
      elsif field.length.is_a?(Symbol) || field.length.is_a?(String)
        length_annotation = annotations.find { |f| f.note == field.length.to_s }
        if !length_annotation
          raise "No annotation called #{field.length} to get the length from"
        end
        length = length_annotation.interpret(binary).int_value
      else
        raise "No way to determine the length for field #{field.name} of type #{interpreter.class}"
      end

      annotation = org.trypticon.hex.anno.SimpleMutableAnnotation.new(pos, length, interpreter, field.name)

      annotations << annotation

      pos += length
    end

    length = pos - position
    org.trypticon.hex.anno.SimpleMutableGroupAnnotation.new(position, length, self.name.to_s, annotations)
  end
end

def structure(name, &block)
  structure = StructureDSL.new(name)

  structure.instance_eval(&block)

  structure
end
