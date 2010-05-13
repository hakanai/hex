
require 'org/trypticon/hex/formats/classfile/attribute_info.rb'

structure :field_info do
  uint16_be :access_flags
  uint16_be :name_index
  uint16_be :descriptor_index
  uint16_be :attributes_count

  array :attributes, :size => :attributes_count, :element_type => :attribute_info
end
