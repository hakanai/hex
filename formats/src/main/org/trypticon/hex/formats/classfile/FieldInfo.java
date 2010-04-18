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
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.binary.Binary;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * "field_info" structure.
 *
 * @author trejkaz
 */
public class FieldInfo extends AbstractClassFileStructure {
    protected FieldInfo() {
        super("field_info");
    }

    public Annotation drop(Binary binary, long position) {
        long pos = position;
        Annotation accessFlags     = u2(pos, "access_flags");     pos += 2;
        Annotation nameIndex       = u2(pos, "name_index");       pos += 2;
        Annotation descriptorIndex = u2(pos, "descriptor_index"); pos += 2;
        Annotation attributesCount = u2(pos, "attributes_count"); pos += 2;

        List<Annotation> attributesChildren = new LinkedList<Annotation>();
        int attributesLength = 0;
        for (int i = 0, count = ((Number) attributesCount.interpret(binary)).intValue(); i < count; i++) {
            Annotation attributesChild = new AttributeInfo().drop(binary, pos + attributesLength);
            attributesLength += attributesChild.getLength();
            attributesChildren.add(attributesChild);
        }
        Annotation attributes = new SimpleMutableGroupAnnotation(pos, attributesLength, "attributes", attributesChildren);
        pos += attributesLength;

        int length = (int) (pos - position);
        return new SimpleMutableGroupAnnotation(position, length, getName(), Arrays.asList(
            accessFlags, nameIndex, descriptorIndex,
            attributesCount, attributes
        ));
    }
}
