
structure :rgb do
  uint8 :r
  uint8 :g
  uint8 :b
end

structure :colour_table do
  array :colour, :size => '2 ** ((:colour_bits & 0x7) + 1)', :element_type => :rgb
end

structure :gif_header do
  # Always "GIF"
  string :gif_magic, :length => 3, :charset => 'US-ASCII'
  # Always "87a" or "89a"
  string :gif_version_magic, :length => 3, :charset => 'US-ASCII'

  uint16_le :logical_screen_width
  uint16_le :logical_screen_height

  uint8 :colour_bits
    # 0        Global Colour Table Flag (GCTF)
    # 1..3     Colour Resolution
    # 4        Sort Flag to Global Colour Table
    # 5..7     Size of Global Colour Table, n where size = 2^(1+n)

  uint8 :background_colour_index

  uint8 :pixel_aspect_ratio

  switch :colour_bits do |value|
    if value & 0x80 != 0
      :colour_table
    else
      nil
    end
  end
end
