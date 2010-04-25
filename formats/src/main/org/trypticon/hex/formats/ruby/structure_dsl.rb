
class Field
  attr_accessor :name
  attr_accessor :interpreter_map
  attr_accessor :length_from_field

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

class InstantiatedField < Field
  attr_accessor :interpreter
  attr_accessor :position
  attr_accessor :length

  def initialize(field)
    @name = field.name
    @interpreter_map = field.interpreter_map
    @length_from_field = field.length_from_field

    @interpreter = $interpreter_storage.from_map(field.interpreter_map)
    if !@interpreter
      raise "Could not find interpreter: #{field.interpreter_map.inspect}"
    end
  end
end

class StructureDSL
  @@fields = []

  def self.nice_name
    @@nice_name || name
  end

  def self.nice_name=(nice_name)
    @@nice_name = nice_name.to_s
  end

  def self.unsigned8(sym)
    @@fields << Field.new(sym, "uint1")
  end

  def self.unsigned16(sym)
    @@fields << Field.new(sym, "uint2be")
  end

  def self.unsigned32(sym)
    @@fields << Field.new(sym, "uint4be")
  end

  def self.string(sym, options = {})
    length = options.delete(:length)
    string_options = { :charset => 'UTF-8' }
    string_options.merge!(options)

    @@fields << Field.new(sym, "string", string_options) do |field|
      if length.is_a?(Symbol)
        field.length_from_field = length
      end
    end
  end

  # Implements Structure
  def drop(binary, position)

    instantiated_fields = []
    annotations = []
    pos = position

    @@fields.each do |field|
      instantiated_field = InstantiatedField.new(field)

      length = -1
      if instantiated_field.interpreter.is_a?(org.trypticon.hex.interpreters.FixedLengthInterpreter)
        length = instantiated_field.interpreter.value_length
      elsif instantiated_field.length_from_field
        length_field = instantiated_fields.find { |f| f.name == instantiated_field.length_from_field }
        length = length_field.interpreter.interpret(binary, length_field.position, length_field.length).int_value
      else
        raise "No way to determine the length for field #{field.name} of type #{instantiated_field.interpreter.class}"
      end

      instantiated_field.position = pos
      instantiated_field.length = length

      annotation = org.trypticon.hex.anno.SimpleMutableAnnotation.new(pos, length, instantiated_field.interpreter, field.name)

      instantiated_fields << instantiated_field
      annotations << annotation

      pos += length
    end

    length = pos - position
    org.trypticon.hex.anno.SimpleMutableGroupAnnotation.new(position, length, self.class.nice_name, annotations)
  end

  def self.metaclass
    class << self
      self
    end
  end
end

def structure(name, &block)
  clazz = Class.new(StructureDSL) do
    instance_eval(&block)
  end
  clazz.nice_name = name
  clazz.new
end
