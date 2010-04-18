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
 * Interpreter for unsigned byte values.
 *
 * @author trejkaz
 */
public class UByteInterpreter extends AbstractFixedLengthInterpreter<UByte> {
    public UByteInterpreter() {
        super(UByte.class, 1);
    }

    public UByte interpret(Binary binary, long position) {
        return new UByte(binary.read(position));
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof UByteInterpreter;
    }

    @Override
    public int hashCode() {
        return 100081;
    }

    @Override
    public String toString() {
        return "uint1";
    }
}
