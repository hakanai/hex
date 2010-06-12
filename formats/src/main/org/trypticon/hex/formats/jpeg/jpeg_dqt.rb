
# DQT = Define Quantisation Table

structure :quantisation_table do
  # Subdivided into 4 bits for number of QT, 4 bits for precision of QT.
  uint8      :table_info

  # 64 * (precision + 1) bytes of quantisation table data
  unknown    :table_data, :length => '64 * (((:table_info >> 4) & 0xF) + 1)'
end

structure :dqt do
  uint16_be  :blockid      # Always FF DB
  uint16_be  :length

  # Length value includes the length we already read.
  array      :tables, :size_type => :fixed_byte_size, :byte_size => ':length - 2', :element_type => :quantisation_table
end
