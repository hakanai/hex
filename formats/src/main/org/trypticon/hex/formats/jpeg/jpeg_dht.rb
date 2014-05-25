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

# DHT = Define Huffman Table

structure :huffman_table do
  # bit 0..3   number of tables
  # bit 4      type of table  0=DC 1=AC
  # bit 5..7   reserved =0
  uint8      :huffman_table_info

  # TODO: A way to represent this as an array and still be able to derive the value below.

  uint8      :length1_count
  uint8      :length2_count
  uint8      :length3_count
  uint8      :length4_count
  uint8      :length5_count
  uint8      :length6_count
  uint8      :length7_count
  uint8      :length8_count
  uint8      :length9_count
  uint8      :length10_count
  uint8      :length11_count
  uint8      :length12_count
  uint8      :length13_count
  uint8      :length14_count
  uint8      :length15_count
  uint8      :length16_count

  unknown    :table_data, :length => ':length1_count + :length2_count + :length3_count + :length4_count +
                                      :length5_count + :length6_count + :length7_count + :length8_count +
                                      :length9_count + :length10_count + :length11_count + :length12_count +
                                      :length13_count + :length14_count + :length15_count + :length16_count'
end


structure :dht do
  uint16_be  :blockid      # Always FF C4
  uint16_be  :length

  # Length value includes the length we already read.
  array      :tables, :size_type => :fixed_byte_size, :byte_size => ':length - 2', :element_type => :huffman_table
end
