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

package org.trypticon.hex.binary;

/**
 * Binary utility methods.
 *
 * @author trejkaz
 */
public class BinaryUtils {
    private BinaryUtils() {
    }

    /**
     * <p>Finds a sequence of bytes in a binary.</p>
     *
     * <p>Warning: this is likely to be very slow if the binary is large.</p>
     *
     * @param binary the binary.
     * @param sequence the sequence of bytes to search for.
     * @return the position at which it was found, or {@code -1} if it was not found.
     */
    public static long positionOf(Binary binary, byte[] sequence) {
        long position = 0;
        long endSearchPosition = binary.length() - sequence.length;
        while (position < endSearchPosition) {
            boolean match = true;
            for (int i = 0; i < sequence.length; i++) {
                if (sequence[i] != binary.read(position + i)) {

                    // This works, but it is naive because it fails to take into account that part of the sequence
                    // we just read may have been the start of the sequence.  Thus we will read some bytes more
                    // than once, whereas really we need only read each byte once at most.
                    match = false;
                    break;
                }
            }

            if (match) {
                return position;
            }

            position++;
        }

        return -1;
    }
}
