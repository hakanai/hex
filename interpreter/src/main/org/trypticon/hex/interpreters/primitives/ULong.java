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

import java.math.BigInteger;

import org.trypticon.hex.interpreters.Value;

/**
 * An unsigned int value.
 *
 * @author trejkaz
 */
public class ULong implements Value {
    private final long value;

    public ULong(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public int length() {
        return 8;
    }

    public String toString() {
        BigInteger signed = BigInteger.valueOf(value);
        if (signed.signum() < 0) {
            signed = signed.add(BigInteger.ONE.shiftLeft(64));
        }
        return signed.toString();
    }
}
