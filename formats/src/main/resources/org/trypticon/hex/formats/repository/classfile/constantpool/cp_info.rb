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

require_relative 'constantpool/CONSTANT_Utf8_info'
require_relative 'constantpool/CONSTANT_Integer_info'
require_relative 'constantpool/CONSTANT_Float_info'
require_relative 'constantpool/CONSTANT_Long_info'
require_relative 'constantpool/CONSTANT_Double_info'
require_relative 'constantpool/CONSTANT_Class_info'
require_relative 'constantpool/CONSTANT_String_info'
require_relative 'constantpool/CONSTANT_Fieldref_info'
require_relative 'constantpool/CONSTANT_Methodref_info'
require_relative 'constantpool/CONSTANT_InterfaceMethodref_info'
require_relative 'constantpool/CONSTANT_NameAndType_info'

structure :cp_info do
  uint8   :tag
  switch  :tag, :replaces_this_structure => true do |value|
    case value
      when  1 ; :CONSTANT_Utf8_info
      when  3 ; :CONSTANT_Integer_info
      when  4 ; :CONSTANT_Float_info
      when  5 ; :CONSTANT_Long_info
      when  6 ; :CONSTANT_Double_info
      when  7 ; :CONSTANT_Class_info
      when  8 ; :CONSTANT_String_info
      when  9 ; :CONSTANT_Fieldref_info
      when 10 ; :CONSTANT_Methodref_info
      when 11 ; :CONSTANT_InterfaceMethodref_info
      when 12 ; :CONSTANT_NameAndType_info
      else raise("Value has no mapping: #{value}")
    end
  end
end
