
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Utf8_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Integer_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Float_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Long_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Double_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Class_Info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_String_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Fieldref_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_Methodref_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_InterfaceMethodref_info'
require 'org/trypticon/hex/formats/classfile/constantpool/CONSTANT_NameAndType_info'

structure :cp_info do
  uint8   :tag
  switch  :tag, :replaces_this_structure => true, :mappings => {
            1  => :CONSTANT_Utf8_info,
            3  => :CONSTANT_Integer_info,
            4  => :CONSTANT_Float_info,
            5  => :CONSTANT_Long_info,
            6  => :CONSTANT_Double_info,
            7  => :CONSTANT_Class_info,
            8  => :CONSTANT_String_info,
            9  => :CONSTANT_Fieldref_info,
            10 => :CONSTANT_Methodref_info,
            11 => :CONSTANT_InterfaceMethodref_info,
            12 => :CONSTANT_NameAndType_info,
          }
end
