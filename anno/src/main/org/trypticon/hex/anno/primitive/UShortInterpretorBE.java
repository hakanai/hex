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

import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.binary.Binary;

/**
 * Interpretor for unsigned short values.
 *
 * @author trejkaz
 */
public class UShortInterpretorBE implements Interpretor<UShort> {
    public Class<UShort> getType() {
        return UShort.class;
    }

    public UShort interpret(Binary binary, long position) {
        return new UShort(BigEndian.getShort(binary, position));
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof UShortInterpretorBE;
    }

    @Override
    public int hashCode() {
        return 100161;
    }

    @Override
    public String toString() {
        return "uint2be";
    }
}