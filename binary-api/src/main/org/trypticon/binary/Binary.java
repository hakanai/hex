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

package org.trypticon.binary;

import java.nio.ByteBuffer;

/**
 * Generic interface to things which contain binary data.
 *
 * @author trejkaz
 */
public interface Binary {

    /**
     * Gets the length of the binary, in bytes.
     *
     * @return the length of the binary, in bytes.
     */
    public long length();

    /**
     * Reads a single byte from a given position.
     *
     * @param position the position to read.
     * @return the single byte.
     */
    public byte read(long position);

    /**
     * Reads bytes from a given position into the buffer provided, filling the entire buffer.
     *
     * @param position tne position to begin reading from.
     * @param buffer the buffer to read into.
     * @param offset the offset in the buffer to begin reading into.
     * @param length the number of bytes to read.
     */
    public void read(long position, byte[] buffer, int offset, int length);

    /**
     * Reads bytes from a given position into the buffer provided, filling the entire buffer.
     *
     * @param position tne position to begin reading from.
     * @param buffer the buffer to read into.
     */
    public void read(long position, byte[] buffer);

    /**
     * Reads bytes from a given position into the buffer provided.
     *
     * @param position the position to begin reading from.
     * @param buffer the buffer to read into.
     * @param length the length to read.
     */
    public void read(long position, ByteBuffer buffer, int length);

}
