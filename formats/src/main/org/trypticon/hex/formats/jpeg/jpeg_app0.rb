
structure :app0 do
  uint16_be  :blockid      # Always FF E0
  uint16_be  :length
  string     :identifier, :length => 5, :charset => 'US-ASCII'

  # Length value includes the length and identifier we just read.
  unknown    :application_data, :length => ':length - 7'
end
