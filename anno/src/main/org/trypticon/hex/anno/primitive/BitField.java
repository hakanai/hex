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

package org.trypticon.hex.anno.primitive;

/**
 * Holds a mask and shift count, useful for computing fields which are at specific
 * bit offsets inside a larger value.
 *
 * @author trejkaz
 */
public class BitField {
    private final int shift;
    private final int nextShift;
    private final int mask;

    private BitField(int shift, int nextShift, int mask) {
        this.shift = shift;
        this.nextShift = nextShift;
        this.mask = mask;
    }

    public int evaluate(int value) {
        return (value >> shift) & mask;
    }

    /**
     * Creates a bit field which contains the first {@code numBits} bits of a short value.
     * Bit fields for the subsequent fields can be retrieved by calling {@link #next(int)}
     * on the initial bit field.
     *
     * @param numBits the number of bits.
     * @return the bit field.
     * @see #next(int)
     */
    public static BitField lowest(int numBits) {
        int shift = 0;
        int mask = (1 << numBits) - 1;
        return new BitField(shift, numBits, mask);
    }

    /**
     * Creates a bit field which contains the next {@code numBits} above the bits
     * contained by this one.
     *
     * @param numBits the number of bits.
     * @return the bit field.
     */
    public BitField next(int numBits) {
        int shift = this.nextShift;
        int mask = (1 << numBits) - 1;
        return new BitField(shift, shift + numBits, mask);
    }
}
