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
# An array structure will create a sequence of the same structure.
#
class ArrayStructure
  attr_reader :name

  def initialize(name, start_index, size_type_object, element_structure)
    @name = name
    @start_index = start_index
    @size_type_object = size_type_object
    @element_structure = element_structure
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)

    annotations = []
    pos = position
    child_drop_context = drop_context.new_child_context(annotations)

    # Size object determines the logic for the loop itself.
    @size_type_object.drop_elements(child_drop_context, @start_index, binary) do |i|
      annotation = @element_structure.do_drop(child_drop_context, binary, pos)
      annotation.note = "#{self.name}[#{i}]"
      annotations << annotation
      pos += annotation.length
      annotation
    end

    length = pos - position

    if length > 0
      SimpleGroupAnnotation.new(position, length, self.name.to_s, annotations)
    else
      nil
    end
  end

  class FixedElementCount
    def initialize(size)
      @size = size
    end

    def drop_elements(drop_context, start_index, binary, &block_for_one_element)
      size = drop_context.get_int_value(:size, @size, binary)
      (start_index...start_index+size).each do |i|
        block_for_one_element.call(i)
      end
    end
  end

  class FixedByteSize
    def initialize(byte_size)
      @byte_size = byte_size
    end

    def drop_elements(drop_context, start_index, binary, &block_for_one_element)
      byte_size = drop_context.get_int_value(:size, @byte_size, binary)
      infinity = 1/0.0
      length = 0
      (start_index...infinity).each do |i|
        annotation = block_for_one_element.call(i)
        length += annotation.length
        break if length >= byte_size
      end
    end
  end

  class UnlimitedElementsUntilException
    def drop_elements(drop_context, start_index, binary, &block_for_one_element)
      infinity = 1/0.0
      (start_index...infinity).each do |i|
        begin
          block_for_one_element.call(i)
        rescue => e
          puts "#{e.message} #{e.backtrace}"
          break
        end
      end
    end
  end


end
