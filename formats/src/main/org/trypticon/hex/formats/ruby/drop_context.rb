
#
# Holds information for a single sequence of annotations being dropped into place.
#
class DropContext
  attr_reader :annotations

  def initialize(annotations)
    @annotations = annotations
  end

  # call-seq:
  #   get_int_value(:length, 2, binary)       #=> an Integer
  #   get_int_value(:length, :length, binary) #=> an Integer
  #   get_int_value(:length, ":length - 1", binary"  #=> an Integer
  #
  # Gets an integer value from a previously encountered annotation.
  #
  # Parameters:
  #   desc - symbol describing the parameter (used primarily for exception messages)
  #   param - the parameter provided:
  #         If it's a number, the number is returned after converting to an integer.
  #         If it's a symbol, the number is taken from the field with that name, which must
  #           have occurred previous to the current field being processed.
  #         If it's a string, it is interpreter as an expression built up of symbols.  The symbols are
  #           first converted to int values in the usual way, and then the expression as a whole is evaluated.
  #   binary - the binary to look up values in if needed
  #
  def get_int_value(desc, param, binary)
    if param.is_a?(Numeric)
      param.to_i
    elsif param.is_a?(Symbol)
      annotation = @annotations.find { |a| a.note == param.to_s }
      if !annotation
        raise "No annotation called #{param} to get the length from"
      end
      annotation.interpret(binary).int_value
    elsif param.is_a?(String)
      while /:[a-z0-9_]+/.match(param)
        param = param.gsub(/:([a-z0-9_]+)/) { |match| get_int_value(desc, $1.to_sym, binary) }
      end
      eval param
    else
      raise "No way to determine the #{desc}"
    end
  end
end
