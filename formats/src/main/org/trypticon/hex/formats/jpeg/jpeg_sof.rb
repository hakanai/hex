
# SOF = Start of Frame

structure :sof_component_info do
  # 1 = Y, 2 = Cb, 3 = Cr, 4 = I, 5 = Q
  uint8 :component_id

  # Further divided into two 4-bit segments for vertical and horizontal.
  uint8 :sampling_factors

  uint8 :quantisation_table_number
end

structure :sof0 do
  uint16_be  :block_id    # Always FF C0
  uint16_be  :length

  uint8      :bits_per_sample
  uint16_be  :image_height
  uint16_be  :image_width
  uint8      :component_count
  array      :components, :size => :component_count, :element_type => :sof_component_info
end

