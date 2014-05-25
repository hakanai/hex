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

# SOF = Start of Frame

structure :sof_component_info do
  # 1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q
  uint8 :component_id

  # Further divided into two 4-bit segments for vertical and horizontal.
  uint8 :sampling_factors

  uint8 :quantisation_table_number
end

structure :sof0 do
  uint16_be  :block_id    # Always FF C0
  uint16_be  :length

  uint8      :bits_per_sample
  uint16_be  :image_height
  uint16_be  :image_width
  uint8      :component_count
  array      :components, :size => :component_count, :element_type => :sof_component_info
end

