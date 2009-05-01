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

package org.trypticon.hex.anno;

import org.trypticon.hex.binary.Binary;

/**
 * Base convenience class for implementing fixed-length interpretors.
 *
 * @param <V> the value type.
 * @author trejkaz
 */
public abstract class AbstractFixedLengthInterpretor<V extends Value>
        extends AbstractInterpretor<V>
        implements FixedLengthInterpretor<V> {

    private final int valueLength;

    protected AbstractFixedLengthInterpretor(Class<V> type, int valueLength) {
        super(type);
        this.valueLength = valueLength;
    }

    public int getValueLength() {
        return valueLength;
    }

    /**
     * Interprets the value for a range.
     *
     * @param binary the binary.
     * @param position the position of the start of the value.
     * @return the value.
     */
    protected abstract V interpret(Binary binary, long position);

    public V interpret(Binary binary, long position, int length) {
        if (length != valueLength) {
            throw new IllegalArgumentException("Only supports values of length " + valueLength
                    + " but got " + length);
        }

        return interpret(binary, position);
    }
}
