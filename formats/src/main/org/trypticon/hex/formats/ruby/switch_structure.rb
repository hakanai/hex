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
# A switch structure will create a different structure based on some value in the structure.
#
class SwitchStructure
  attr_reader :value_name
  attr_reader :replaces_this_structure

  def initialize(value_name, replaces_this_structure, mapping_block)
    @value_name = value_name
    @replaces_this_structure = replaces_this_structure
    @mapping_block = mapping_block
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)
    value = drop_context.get_int_value(:value, @value_name, binary)

    structure_name = @mapping_block.call(value)
    if structure_name
      structure = $local_structure_storage[structure_name] || raise("Structure '#{structure_name}' has not been defined")
      structure.do_drop(drop_context, binary, position)
    else
      nil
    end
  end
end
