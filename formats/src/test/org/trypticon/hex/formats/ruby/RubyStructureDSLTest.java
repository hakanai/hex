/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.trypticon.hex.formats.ruby;

import java.util.Arrays;

import org.junit.Test;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;
import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.interpreters.primitives.unsigned.UByteInterpreter;
import org.trypticon.hex.interpreters.primitives.unsigned.UIntInterpreterBE;
import org.trypticon.hex.interpreters.primitives.unsigned.UShortInterpreterBE;
import org.trypticon.hex.interpreters.strings.StringInterpreter;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link RubyStructureDSL}.
 *
 * @author trejkaz
 */
public class RubyStructureDSLTest {

    @Test
    public void testLengthUsage() {
        Structure structure = RubyStructureDSL.load(
            "structure :string_with_length do\n" +
            "  uint16_be  :length\n" +
            "  string     :bytes, :charset => 'UTF-8', :length => :length\n" +
            "end"
        );

        Binary binary = BinaryFactory.wrap(new byte[] {
            0, 4,                     // length = 4
            0x74, 0x65, 0x73, 0x74    // bytes = "test"
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] children = {
            new SimpleMutableAnnotation(0, 2, new UShortInterpreterBE(), "length"),
            new SimpleMutableAnnotation(2, 4, new StringInterpreter("UTF-8"), "bytes"),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 6, "string_with_length", Arrays.asList(children));

        assertEquals("Wrong annotations created", expected, group);
    }

    @Test
    public void testArrayUsage() {
        Structure structure = RubyStructureDSL.load(
            "structure :array_with_size do\n" +
            "  uint16_be :size \n" +
            "  array     :elements, :element_type => :uint16_be, :start_index => 1, :size => :size \n" +
            "end"
        );

        Binary binary = BinaryFactory.wrap(new byte[] {
            0, 3,               // size = 3
            0, 1, 0, 2, 0, 3,   // elements = [1, 2, 3]
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] arrayElements = {
            new SimpleMutableAnnotation(2, 2, new UShortInterpreterBE(), "elements[1]"),
            new SimpleMutableAnnotation(4, 2, new UShortInterpreterBE(), "elements[2]"),
            new SimpleMutableAnnotation(6, 2, new UShortInterpreterBE(), "elements[3]"),
        };
        Annotation[] children = {
            new SimpleMutableAnnotation(0, 2, new UShortInterpreterBE(), "size"),
            new SimpleMutableGroupAnnotation(2, 6, "elements", Arrays.asList(arrayElements)),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 8, "array_with_size", Arrays.asList(children));

        assertEquals("Wrong annotations created", expected, group);
    }

    private Structure createSwitchNotReplacingStructure() {
        return RubyStructureDSL.load(
            "structure :option1 do\n" +
            "  uint16_be :value\n" +
            "end\n" +
            " \n" +
            "structure :option2 do\n" +
            "  uint32_be :value\n" +
            "end\n" +
            " \n" +
            "structure :one_or_the_other do\n" +
            "  uint8 :tag\n" +
            "  switch :tag do |value|\n" +
            "    case value \n" +
            "      when 1; :option1 \n" +
            "      when 2; :option2 \n" +
            "    end\n" +
            "  end\n" +
            "end\n"
        );
    }

    @Test
    public void testSwitchUsageNotReplacingStructure1() {
        Structure structure = createSwitchNotReplacingStructure();

        Binary binary = BinaryFactory.wrap(new byte[] {
            1,     // tag = 1
            0, 1,  // value = 1
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] grandchildren = {
            new SimpleMutableAnnotation(1, 2, new UShortInterpreterBE(), "value"),
        };
        Annotation[] children = {
            new SimpleMutableAnnotation(0, 1, new UByteInterpreter(), "tag"),
            new SimpleMutableGroupAnnotation(1, 2, "option1", Arrays.asList(grandchildren)),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 3, "one_or_the_other", Arrays.asList(children));
        assertEquals("Wrong annotations created", expected, group);
    }

    @Test
    public void testSwitchUsageNotReplacingStructure2() {
        Structure structure = createSwitchNotReplacingStructure();

        Binary binary = BinaryFactory.wrap(new byte[] {
            2,           // tag = 2
            0, 0, 0, 2,  // value = 2
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] grandchildren = {
            new SimpleMutableAnnotation(1, 4, new UIntInterpreterBE(), "value"),
        };
        Annotation[] children = {
            new SimpleMutableAnnotation(0, 1, new UByteInterpreter(), "tag"),
            new SimpleMutableGroupAnnotation(1, 4, "option2", Arrays.asList(grandchildren)),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 5, "one_or_the_other", Arrays.asList(children));
        assertEquals("Wrong annotations created", expected, group);
    }

    private Structure createSwitchReplacingStructure() {
        return RubyStructureDSL.load(
            "structure :option1 do\n" +
            "  uint8      :tag\n" +
            "  uint16_be  :value\n" +
            "end\n" +
            " \n" +
            "structure :option2 do\n" +
            "  uint8      :tag\n" +
            "  uint32_be  :value\n" +
            "end\n" +
            " \n" +
            "structure :one_or_the_other do\n" +
            "  uint8  :tag\n" +
            "  switch :tag, :replaces_this_structure => true do |value|\n" +
            "    case value \n" +
            "      when 1; :option1 \n" +
            "      when 2; :option2 \n" +
            "    end\n" +
            "  end\n" +
            "end\n"
        );
    }

    @Test
    public void testSwitchUsageReplacingStructure1() {
        Structure structure = createSwitchReplacingStructure();

        Binary binary = BinaryFactory.wrap(new byte[] {
            1,     // tag = 1
            0, 1,  // value = 1
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] children = {
            new SimpleMutableAnnotation(0, 1, new UByteInterpreter(), "tag"),
            new SimpleMutableAnnotation(1, 2, new UShortInterpreterBE(), "value"),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 3, "option1", Arrays.asList(children));
        assertEquals("Wrong annotations created", expected, group);
    }

    @Test
    public void testSwitchUsageReplacingStructure2() {
        Structure structure = createSwitchReplacingStructure();

        Binary binary = BinaryFactory.wrap(new byte[] {
            2,           // tag = 2
            0, 0, 0, 2,  // value = 2
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] children = {
            new SimpleMutableAnnotation(0, 1, new UByteInterpreter(), "tag"),
            new SimpleMutableAnnotation(1, 4, new UIntInterpreterBE(), "value"),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 5, "option2", Arrays.asList(children));
        assertEquals("Wrong annotations created", expected, group);
    }

    @Test
    public void testSwitchUsageForOptionalStructure() {
        Structure structure = RubyStructureDSL.load(
            "structure :optional_structure do\n" +
            "  uint32_be  :value\n" +
            "end\n" +
            " \n" +
            "structure :some_header do\n" +
            "  uint8  :bit_field\n" +
            "  switch :bit_field do |value|\n" +
            "    if value & 0x01 != 0 \n" +
            "      :optional_structure \n" +
            "    else \n" +
            "      nil \n" +
            "    end\n" +
            "  end\n" +
            "end\n"
        );

        Binary binary = BinaryFactory.wrap(new byte[] {
            2,           // bit_field = 2 (0x01 not set)
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] children = {
            new SimpleMutableAnnotation(0, 1, new UByteInterpreter(), "bit_field"),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 1, "some_header", Arrays.asList(children));
        assertEquals("Wrong annotations created", expected, group);
    }
}
