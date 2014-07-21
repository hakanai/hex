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

# DQT = Define Quantisation Table

structure :quantisation_table do
  # Subdivided into 4 bits for number of QT, 4 bits for precision of QT.
  uint8      :table_info

  # 64 * (precision + 1) bytes of quantisation table data
  unknown    :table_data, :length => '64 * (((:table_info >> 4) & 0xF) + 1)'
end

structure :dqt do
  uint16_be  :blockid      # Always FF DB
  uint16_be  :length

  # Length value includes the length we already read.
  array      :tables, :size_type => :fixed_byte_size, :byte_size => ':length - 2', :element_type => :quantisation_table
end
