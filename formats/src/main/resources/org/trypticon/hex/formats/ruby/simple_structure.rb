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

    raise("length is negative (#{length})") if length < 0

    SimpleAnnotation.new(position, length, @interpreter).tap do |a|
      a.note = @name.to_s
    end
  end
end
