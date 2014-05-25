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

structure :rgb do
  uint8 :r
  uint8 :g
  uint8 :b
end

structure :colour_table do
  array :colour, :size => '2 ** ((:colour_bits & 0x7) + 1)', :element_type => :rgb
end

structure :gif_header do
  # Always "GIF"
  string :gif_magic, :length => 3, :charset => 'US-ASCII'
  # Always "87a" or "89a"
  string :gif_version_magic, :length => 3, :charset => 'US-ASCII'

  uint16_le :logical_screen_width
  uint16_le :logical_screen_height

  uint8 :colour_bits
    # 0        Global Colour Table Flag (GCTF)
    # 1..3     Colour Resolution
    # 4        Sort Flag to Global Colour Table
    # 5..7     Size of Global Colour Table, n where size = 2^(1+n)

  uint8 :background_colour_index

  uint8 :pixel_aspect_ratio

  switch :colour_bits do |value|
    if value & 0x80 != 0
      :colour_table
    else
      nil
    end
  end
end
