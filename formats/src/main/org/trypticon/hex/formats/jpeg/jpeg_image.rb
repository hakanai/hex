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

require 'org/trypticon/hex/formats/jpeg/jpeg_sof'
require 'org/trypticon/hex/formats/jpeg/jpeg_dht'
require 'org/trypticon/hex/formats/jpeg/jpeg_soi'
require 'org/trypticon/hex/formats/jpeg/jpeg_eoi'
require 'org/trypticon/hex/formats/jpeg/jpeg_sos'
require 'org/trypticon/hex/formats/jpeg/jpeg_dqt'
require 'org/trypticon/hex/formats/jpeg/jpeg_app0'

structure :jpeg_block do
  uint16_be  :block_id
  switch  :block_id, :replaces_this_structure => true do |value|
    case value
      when 0xFFC0 ; :sof0
      when 0xFFC4 ; :dht
      when 0xFFD8 ; :soi
      when 0xFFD9 ; :eoi
      when 0xFFDA ; :sos
      when 0xFFDB ; :dqt
      when 0xFFE0 ; :app0
      else raise("Value has no mapping: #{"%04X" % value}")
    end
  end
end

structure :jpeg_image do
  array :blocks, :size_type => :until_exception, :element_type => :jpeg_block
end
