
#
# An array structure will create a sequence of the same structure.
#
class ArrayStructure
  attr_reader :name

  def initialize(name, start_index, size, element_structure)
    @name = name
    @start_index = start_index
    @size = size
    @element_structure = element_structure
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)

    annotations = []
    pos = position
    size = drop_context.get_int_value(:size, @size, binary)

    child_drop_context = DropContext.new(annotations)

    (@start_index...@start_index+size).each do |i|

      annotation = @element_structure.do_drop(child_drop_context, binary, pos)
      annotation.note = "#{self.name}[#{i}]"

      annotations << annotation

      pos += annotation.length
    end

    length = pos - position
    SimpleMutableGroupAnnotation.new(position, length, self.name.to_s, annotations)
  end
end
