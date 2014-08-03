#
# Hex - a hex viewer and annotator
# Copyright (C) 2009-2014  Trejkaz, Hex Project
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

require 'java'

java_import org.trypticon.hex.anno.CommonAttributes
java_import org.trypticon.hex.anno.SimpleAnnotation
java_import org.trypticon.hex.anno.SimpleGroupAnnotation
java_import org.trypticon.hex.interpreters.FixedLengthInterpreter
java_import org.trypticon.hex.formats.Structure

require_relative 'drop_context'
require_relative 'simple_structure'
require_relative 'array_structure'
require_relative 'switch_structure'


# $interpreter_storage is defined by the container.  We define a local structure storage here for
# structures which are defined in the script.
$local_structure_storage ||= {}


# Extensions for SimpleAnnotation to reduce boilerplate.
class SimpleAnnotation
  def note
    # Workaround here for weird JRuby behaviour. Somehow this method returns Symbol
    self.get(CommonAttributes.NOTE).to_s
  end

  def note=(note)
    self.set(CommonAttributes.NOTE, note)
  end
end


#
# Main DSL class.
#
class StructureDSL
  attr_reader :name
  attr_reader :fields

  def initialize(name)
    @name = name
    @fields = []
  end

  # Catch-all allows simple interpreter-based fields to be supported without creating methods for them all.
  #
  # Auto-creates methods with the following arguments:
  #   name     - the name of the field
  #   options  - additional options for the interpreter - differs depending on the interpreter being used.
  #     (the method name is used as the interpreter name.)
  #
  def method_missing(method, *args)
    # name of the method call becomes the interpreter name
    # first argument becomes the field name
    # the tail goes into the options for the field

    StructureDSL.send(:define_method, method) do |name, *args1|
      options = args1.shift || {}
      options_with_name = options.merge(:name => method)
      @fields << SimpleStructure.new(name, options_with_name)
    end

    send(method, *args)
  end

  # Creates an unknown structure.  Actually this is just a shortcut for specifying the null interpreter.
  def unknown(name, options = {})
    interpreter_map_with_name = options.merge(:name => :null)
    @fields << SimpleStructure.new(name, interpreter_map_with_name)
  end

  # Creates an array structure.
  #
  #   name     - the name the array structure will receive when dropped into an annotation set.  The elements
  #              will be named after the array structure itself.
  #   options  - a map of options.  Supported options:
  #     :size          - the size of the array, i.e. the number of elements.
  #     :element_type  - the type of each element in the array
  #     :start_index   - the start index (default: 0)
  #
  def array(name, options = {})
    size_type    = options[:size_type]    || :fixed_element_count
    element_type = options[:element_type] || raise("element_type option not provided")
    start_index  = options[:start_index]  || 0

    size_type_object = case size_type
      when :fixed_element_count
        size       = options[:size]         || raise("size option not provided")
        ArrayStructure::FixedElementCount.new(size)
      when :fixed_byte_size
        byte_size  = options[:byte_size]    || raise("byte_size option not provided")
        ArrayStructure::FixedByteSize.new(byte_size)
      when :until_exception
        ArrayStructure::UnlimitedElementsUntilException.new
      else
        raise "Unknown size_type: #{size_type}"
    end

    element_structure = $local_structure_storage[element_type]
    if !element_structure
      # Name not needed here because ArrayStructure stamps its own name onto the elements.
      element_structure = SimpleStructure.new(nil, { :name => element_type })
    end

    @fields << ArrayStructure.new(name, start_index, size_type_object, element_structure)
  end

  # Creates a switch structure (similar to a union but the lengths can vary.)
  # TODO: Is there a better name for this?  variant?
  #
  #   value_name - the name of the field to switch on.  needs to have been defined before the switch definition.
  #   options    - a map of options.  Supported options:
  #     :replaces_this_structure  - if true (default is false), the switched structure replaces this one entirely.
  #                                 If this is used then no other definitions should be included except for the
  #                                 minimum required before the switch.  If false, the structure is added as a
  #                                 child of this structure.
  #   mapping_block - block is called with the value read, and should return a symbol indicating the structure to use.
  #
  def switch(value_name, options = {}, &mapping_block)
    replaces_this_structure = options[:replaces_this_structure] || false

    @fields << SwitchStructure.new(value_name, replaces_this_structure, mapping_block)
  end

  # Drops the structure at the given location.
  def do_drop(drop_context, binary, position)

    annotations = []
    pos = position

    drop_context = drop_context.new_child_context(annotations)

    @fields.each do |field|
      # Special case, if the current structure had a switch definition in it which is supposed to
      # replace our entire structure, then we have to pass it the position of the start of this structure,
      # and then return only *its* annotation instead of our own.
      if field.is_a?(SwitchStructure) && field.replaces_this_structure
        annotation = field.do_drop(drop_context, binary, position)
        return annotation
      end

      annotation = field.do_drop(drop_context, binary, pos)
      if annotation
        annotations << annotation
        pos += annotation.length
      end
    end

    length = pos - position
    SimpleGroupAnnotation.new(position, length, annotations).tap do |a|
      a.note = self.name.to_s
    end
  end

  # Implements Structure
  def drop(binary, position)
    do_drop(DropContext.new([]), binary, position)
  end

  def inspect
    "#{super} name=#{name}"
  end
end

# Main entry point, a simple method which instantiates the DSL and evaluates the caller's block
# against the DSL.
#
# Parameter:
#   name - the name of the structure.
#
def structure(name, &block)
  structure = StructureDSL.new(name)

  structure.instance_eval(&block)

  $local_structure_storage[name] = structure

  structure
end
