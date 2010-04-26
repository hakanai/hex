/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

import org.junit.Test;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;
import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.interpreters.primitives.UByteInterpreter;
import org.trypticon.hex.interpreters.primitives.UShortInterpreterBE;
import org.trypticon.hex.interpreters.strings.StringInterpreter;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link RubyStructureDSL}.
 *
 * @author trejkaz
 */
public class RubyStructureDSLTest {

    @Test
    public void testRunning() {
        Structure structure = new RubyStructureDSL(
            "structure :CONSTANT_Utf8_info do\n" +
            "  uint1    :tag\n" +
            "  uint2be  :length\n" +
            "  string   :bytes, :charset => 'UTF-8', :length => :length\n" +
            "end").createStructure();

        Binary binary = BinaryFactory.wrap(new byte[] {
            1,                        // tag
            0, 4,                     // length (4)
            0x74, 0x65, 0x73, 0x74    // "test"
        });

        GroupAnnotation group = (GroupAnnotation) structure.drop(binary, 0);

        Annotation[] children = {
            new SimpleMutableAnnotation(0, 1, new UByteInterpreter(), "tag"),
            new SimpleMutableAnnotation(1, 2, new UShortInterpreterBE(), "length"),
            new SimpleMutableAnnotation(3, 4, new StringInterpreter("UTF-8"), "bytes"),
        };

        GroupAnnotation expected = new SimpleMutableGroupAnnotation(0, 7, "CONSTANT_Utf8_info", Arrays.asList(children));

        assertEquals("Wrong annotations created", expected, group);
    }
}
