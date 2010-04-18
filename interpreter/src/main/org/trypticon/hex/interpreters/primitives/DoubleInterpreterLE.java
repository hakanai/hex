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

package org.trypticon.hex.interpreters.primitives;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.interpreters.AbstractFixedLengthInterpreter;

/**
 * Interprets 8-byte floating point numbers in IEEE754 format.
 *
 * @author trejkaz
 */
public class DoubleInterpreterLE extends AbstractFixedLengthInterpreter<Double> {
    public DoubleInterpreterLE() {
        super(Double.class, 8);
    }

    public Double interpret(Binary binary, long position) {
        return new Double(java.lang.Double.longBitsToDouble(LittleEndian.getLong(binary, position)));
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DoubleInterpreterLE;
    }

    @Override
    public int hashCode() {
        return 101642;
    }

    @Override
    public String toString() {
        return "float8le";
    }
}
