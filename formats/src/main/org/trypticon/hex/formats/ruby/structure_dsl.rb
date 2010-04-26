
class Field
  attr_accessor :name
  attr_accessor :interpreter_map
  attr_accessor :length

  def initialize(name, interpreter, options = {}, &block)
    @name = name

    # Converts the keys to string in the process.  Symbols look better for hash keys on the Ruby side,
    # but the Java side expects a map keyed by String.
    @interpreter_map = { 'name' => interpreter }
    if options && !options.empty?
      options.each_pair do |key, value|
        @interpreter_map[key.to_s] = value
      end
    end

    if block
      block.call(self)
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

  def unsigned8(sym)
    @fields << Field.new(sym, "uint1")
  end

  def unsigned16(sym)
    @fields << Field.new(sym, "uint2be")
  end

  def unsigned32(sym)
    @fields << Field.new(sym, "uint4be")
  end

  def string(sym, options = {})
    length = options.delete(:length)
    string_options = { :charset => 'UTF-8' }
    string_options.merge!(options)

    @fields << Field.new(sym, "string", string_options) do |field|
      field.length = length
    end
  end

  # Implements Structure
  def drop(binary, position)

    annotations = []
    pos = position

    @fields.each do |field|
      interpreter = $interpreter_storage.from_map(field.interpreter_map)

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
