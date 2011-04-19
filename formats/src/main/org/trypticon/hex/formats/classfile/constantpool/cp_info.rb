
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Utf8_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Integer_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Float_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Long_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Double_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Class_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_String_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Fieldref_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Methodref_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_InterfaceMethodref_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_NameAndType_info'

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
