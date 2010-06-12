
require 'org/trypticon/hex/formats/jpeg/jpeg_sof'
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
