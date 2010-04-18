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

package org.trypticon.hex.formats.classfile.constantpool;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.formats.classfile.AbstractClassFileStructure;
import org.trypticon.hex.interpreters.strings.StringInterpreter;

import java.util.Arrays;

/**
 * "CONSTANT_Utf8_info" structure.
 *
 * @author trejkaz
 */
public class ConstantUtf8Info extends AbstractClassFileStructure {
    protected ConstantUtf8Info() {
        super("CONSTANT_Utf8_info");
    }

    public Annotation drop(Binary binary, long position) {
        long pos = position;
        Annotation tag    = u1(pos, "tag");    pos += 1;
        Annotation length = u2(pos, "length"); pos += 2;

        // The string is actually documented as
        // TODO: Confirm that old-style modified UTF-8 strings work (the ones which Java encoded \0 as two bytes.)
        int lengthValue = ((Number) length.interpret(binary)).intValue();
        Annotation bytes  = new SimpleMutableAnnotation(pos, lengthValue, new StringInterpreter("UTF-8"), "bytes");
        pos += lengthValue;

        int structureLength = (int) (pos - position);
        return new SimpleMutableGroupAnnotation(position, structureLength, getName(),
                                                Arrays.asList(tag, length, bytes));
    }
}
