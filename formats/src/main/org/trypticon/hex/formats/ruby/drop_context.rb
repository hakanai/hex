#
# Hex - a hex viewer and annotator
# Copyright (C) 2009-2014  Trejkaz, Hex Project
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

#
# Holds information for a single sequence of annotations being dropped into place.
#
class DropContext
  attr_reader :annotations

  def initialize(annotations, parent_context = nil)
    @annotations = annotations
    @parent_context = parent_context
  end

  # Creates a new child context.
  def new_child_context(annotations)
    DropContext.new(annotations, self)
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
      annotation = find_annotation(param)
      if !annotation
        raise "No annotation called #{param} to get the #{desc} from"
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

protected

  # Finds an annotation with the given name.  If it doesn't exist in the current context, walks up
  # until it finds a context where it does exist.
  def find_annotation(name)
    annotation = @annotations.find { |a| a.note == name.to_s }
    if !annotation && @parent_context
      annotation = @parent_context.find_annotation(name)
    end
    annotation
  end

end
