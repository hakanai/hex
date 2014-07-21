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

require_relative 'constantpool/cp_info'
require_relative 'field_info'
require_relative 'method_info'
require_relative 'attribute_info'

structure :class_file do

  # Always 0xCAFEBABE.   TODO: Do we want a way to declaratively check that?
  uint32_be  :magic

  uint16_be  :minor_version
  uint16_be  :major_version

  uint16_be  :constant_pool_count
  array      :constant_pool, :size => ':constant_pool_count - 1', :start_index => 1, :element_type => :cp_info

  uint16_be  :access_flags
  uint16_be  :this_class
  uint16_be  :super_class

  uint16_be  :interfaces_count
  array      :interfaces, :size => :interfaces_count, :element_type => :uint16_be

  uint16_be  :fields_count
  array      :fields, :size => :fields_count, :element_type => :field_info

  uint16_be  :methods_count
  array      :methods, :size => :methods_count, :element_type => :method_info

  uint16_be  :attributes_count
  array :attributes, :size => :attributes_count, :element_type => :attribute_info
end
