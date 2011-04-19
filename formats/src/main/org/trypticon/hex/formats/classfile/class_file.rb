
require 'org/trypticon/hex/formats/classfile/constantpool/cp_info'
require 'org/trypticon/hex/formats/classfile/field_info'
require 'org/trypticon/hex/formats/classfile/method_info'
require 'org/trypticon/hex/formats/classfile/attribute_info'

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
