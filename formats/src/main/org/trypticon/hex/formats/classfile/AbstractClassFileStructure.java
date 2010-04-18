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

package org.trypticon.hex.formats.classfile;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.formats.ConvenienceStructure;
import org.trypticon.hex.interpreters.primitives.DoubleInterpreterBE;
import org.trypticon.hex.interpreters.primitives.FloatInterpreterBE;
import org.trypticon.hex.interpreters.primitives.UByteInterpreter;
import org.trypticon.hex.interpreters.primitives.UIntInterpreterBE;
import org.trypticon.hex.interpreters.primitives.ULongInterpreterBE;
import org.trypticon.hex.interpreters.primitives.UShortInterpreterBE;

/**
 * Convenience methods specific to class files to match the terminology used in the Java docs.
 *
 * @author trejkaz
 */
public abstract class AbstractClassFileStructure extends ConvenienceStructure {
    protected AbstractClassFileStructure(String name) {
        super(name);
    }

    protected Annotation u1(long position, String name) {
        return new SimpleMutableAnnotation(position, 1, new UByteInterpreter(), name);
    }

    protected Annotation u2(long position, String name) {
        return new SimpleMutableAnnotation(position, 2, new UShortInterpreterBE(), name);
    }

    protected Annotation u4(long position, String name) {
        return new SimpleMutableAnnotation(position, 4, new UIntInterpreterBE(), name);
    }

    protected Annotation u8(long position, String name) {
        return new SimpleMutableAnnotation(position, 8, new ULongInterpreterBE(), name);
    }

    protected Annotation f4(long position, String name) {
        return new SimpleMutableAnnotation(position, 4, new FloatInterpreterBE(), name);
    }

    protected Annotation f8(long position, String name) {
        return new SimpleMutableAnnotation(position, 8, new DoubleInterpreterBE(), name);
    }
}
