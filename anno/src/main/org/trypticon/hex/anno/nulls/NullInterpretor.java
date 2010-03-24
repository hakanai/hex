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

package org.trypticon.hex.anno.nulls;

import org.trypticon.hex.anno.AbstractInterpretor;
import org.trypticon.hex.binary.Binary;

/**
 * An interpretor which can mark a range as meaning nothing.  Useful for
 * when you have a non-semantic comment with no value, such as "reserved",
 * or "I don't know what this is."
 *
 * @author trejkaz
 */
public class NullInterpretor extends AbstractInterpretor<NullValue> {

    public NullInterpretor() {
        super(NullValue.class);
    }

    public NullValue interpret(Binary binary, long position, int length) {
        return new NullValue(length);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof NullInterpretor;
    }

    @Override
    public int hashCode() {
        return 3425671;
    }

    @Override
    public String toString() {
        return "null";
    }
}
