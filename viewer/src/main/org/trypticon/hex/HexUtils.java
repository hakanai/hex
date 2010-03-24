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

package org.trypticon.hex;

import org.trypticon.hex.binary.Binary;

/**
 * Utilities for converting hex to text.
 *
 * @author trejkaz
 */
public class HexUtils {
    private static final char[] digits = "0123456789ABCDEF".toCharArray();
    private static final char[] ascii;

    static {
        ascii = new char[256];
        for (int i = 0; i < 32; i++) {
            ascii[i] = '.';
        }
        for (int i = 32; i < 128; i++) {
            ascii[i] = (char) i;
        }
        for (int i = 128; i < 256; i++) {
            ascii[i] = '.';
        }
    }

    /**
     * Converts a byte to uppercase hexadecimal.
     *
     * @param b the byte.
     * @return the uppercase hex string.
     */
    public static String toHex(byte b) {
        StringBuilder builder = new StringBuilder(2);
        toHex(b, builder);
        return builder.toString();
    }

    /**
     * Converts a byte to uppercase hexadecimal.
     *
     * @param b the byte.
     * @param builder a string builder to append the hexadecimal string to.
     */
    private static void toHex(byte b, StringBuilder builder) {
        int tmp = (int) b & 0xFF;
        builder.append(digits[(tmp >> 4) & 0x0f]);
        builder.append(digits[(tmp & 0x0f)]);
    }

    /**
     * Converts a byte to a readable ASCII version.  Non-printable characters will be left as the placeholder '.'.
     *
     * @param b the byte.
     * @return the ASCII version.
     */
    public static String toAscii(byte b) {
        int tmp = (int) b & 0xFF;
        return String.valueOf(ascii[tmp]);
    }

    /**
     * Converts the specified region of the binary into a single hex string with spaces separating it.
     *
     * @param binary the binary.
     * @param start the start position, inclusive.
     * @param end the end position, inclusive.
     * @return the hex string.
     */
    public static String toHex(Binary binary, long start, long end) {
        if (start < 0) {
            throw new IllegalArgumentException("start (" + start + ") < 0");
        }
        if (end >= binary.length()) {
            throw new IllegalArgumentException("start (" + start + ") >= binary.length() (" + binary.length() + ")");
        }
        if (start > end) {
            throw new IllegalArgumentException("start (" + start + ") > end (" + end + ")");
        }

        long length = end - start + 1;
        if (length * 3 - 1 > Integer.MAX_VALUE) {
            throw new OutOfMemoryError("Not enough space in a string to fit " + start + ".." + end);
        }

        StringBuilder builder = new StringBuilder((int) length * 3 - 1);
        long pos = start;
        toHex(binary.read(pos), builder);
        for (pos = start + 1; pos <= end; pos++) {
            builder.append(' ');
            toHex(binary.read(pos), builder);
        }

        return builder.toString();
    }
}
