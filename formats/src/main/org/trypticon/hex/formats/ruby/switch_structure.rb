
#
# A switch structure will create a different structure based on some value in the structure.
#
class SwitchStructure
  attr_reader :value_name
  attr_reader :replaces_this_structure

  def initialize(value_name, replaces_this_structure, mapping_block)
    @value_name = value_name
    @replaces_this_structure = replaces_this_structure
    @mapping_block = mapping_block
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)
    value = drop_context.get_int_value(:value, @value_name, binary)

    structure_name = @mapping_block.call(value)
    if structure_name
      structure = $local_structure_storage[structure_name] || raise("Structure '#{structure_name}' has not been defined")
      structure.do_drop(drop_context, binary, position)
    else
      nil
    end
  end
end
