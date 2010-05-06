
structure :CONSTANT_Utf8_info do
  uint8      :tag
  uint16_be  :length

  # The string is actually documented as a byte array.
  # TODO: Confirm that old-style modified UTF-8 strings work (the ones which Java encoded \0 as two bytes.)
  string     :bytes, :length => :length, :charset => 'UTF-8'
end
