/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

package org.trypticon.hex.anno.primitive;

import org.trypticon.hex.anno.AbstractFixedLengthInterpretor;
import org.trypticon.hex.binary.Binary;

/**
 * Interpretor for unsigned long values.
 *
 * @author trejkaz
 */
public class ULongInterpretorBE extends AbstractFixedLengthInterpretor<ULong> {
    public ULongInterpretorBE() {
        super(ULong.class, 2);
    }

    public Class<ULong> getType() {
        return ULong.class;
    }

    public ULong interpret(Binary binary, long position) {
        return new ULong(BigEndian.getLong(binary, position));
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ULongInterpretorBE;
    }

    @Override
    public int hashCode() {
        return 100641;
    }

    @Override
    public String toString() {
        return "uint8be";
    }
}