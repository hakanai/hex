
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
