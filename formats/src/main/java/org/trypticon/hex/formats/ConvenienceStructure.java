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

package org.trypticon.hex.formats;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.CommonAttributes;
import org.trypticon.hex.anno.SimpleAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.interpreters.FixedLengthInterpreter;
import org.trypticon.hex.interpreters.Value;

/**
 * Adds additional convenience for defining structures.  The goal is for this to grow into a form of internal DSL.
 *
 * @author trejkaz
 */
public abstract class ConvenienceStructure extends AbstractStructure {
    protected ConvenienceStructure(String name) {
        super(name);
    }

    protected void checkMagic(Binary binary, Annotation annotation, int magicValue) {
        Value value = annotation.interpret(binary);
        if (((Number) value).intValue() != magicValue) {
            String numFormat = String.format("0x%%0%dx", value.length() * 2);
            throw new IllegalArgumentException(String.format("Magic number " + numFormat + " not found, actually got: " + numFormat,
                                                             magicValue,
                                                             ((Number) value).intValue()));
        }
    }

    protected Annotation magic(Binary binary, long position, FixedLengthInterpreter interpreter, String name, int magicValue) {
        long valueLength = interpreter.getValueLength();
        //TODO: Might be worth making Annotation and GroupAnnotation factories via AnnotationCollection.
        Annotation magicAnnotation = new SimpleAnnotation(position, valueLength, interpreter);
        magicAnnotation.set(CommonAttributes.NOTE, name);
        checkMagic(binary, magicAnnotation, magicValue);
        return magicAnnotation;
    }
}
