
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
