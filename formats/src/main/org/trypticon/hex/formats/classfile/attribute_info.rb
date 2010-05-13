
structure :attribute_info do
  uint16_be :attribute_name_index
  uint32_be :attribute_length

  unknown :attribute_data, :length => :attribute_length
end
