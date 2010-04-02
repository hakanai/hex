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

/**
 * Utilities for building primitives from bytes.
 *
 * @author trejkaz
 */
public class Primitives {
    static short getShort(byte b1, byte b2) {
        return (short) getUShort(b1, b2);
    }

    static int getUShort(byte b1, byte b2) {
        return ((b1 & 0xFF) << 8) | (b2 & 0xFF);
    }

    static int getInt(byte b1, byte b2, byte b3, byte b4) {
        return ((b1 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);
    }

    static long getLong(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8) {
        return (long) getInt(b1, b2, b3, b4) << 32 | getInt(b5, b6, b7, b8);
    }
}
