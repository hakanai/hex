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

package org.trypticon.hex.formats.jpeg;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.OverlappingAnnotationException;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.formats.AbstractStructure;
import org.trypticon.hex.interpreters.primitives.UShort;
import org.trypticon.hex.interpreters.primitives.UShortInterpreterBE;
import org.trypticon.hex.interpreters.strings.StringInterpreter;

import java.util.Arrays;

/**
 * JPEG/JFIF APP0 marker.
 *
 * @author trejkaz
 */
public class JpegAPP0 extends AbstractStructure {
    public JpegAPP0() {
        super("JPEG APP0");
    }

    public void drop(Binary binary, AnnotationCollection annotations, long position) throws OverlappingAnnotationException {
        //FF E0
        Annotation blockId = new SimpleMutableAnnotation(position, 2, new UShortInterpreterBE(), "APP0");
        if (((UShort) blockId.interpret(binary)).getValue() != (short) 0xFFE0)
        {
            throw new IllegalArgumentException("Magic number 0xFFE0 not found, actually got: " + ((UShort) blockId.interpret(binary)).getValue());
        }

        Annotation length = new SimpleMutableAnnotation(position + 2, 2, new UShortInterpreterBE(), "length");
        int lengthValue = ((Number) length.interpret(binary)).intValue();

        Annotation identifier = new SimpleMutableAnnotation(position + 4, 5, new StringInterpreter("US-ASCII"), null);

        GroupAnnotation soi = new SimpleMutableGroupAnnotation(position, 2 + lengthValue, "APP0",
                                                               Arrays.asList(blockId, length, identifier));
        annotations.add(soi);
    }
}
