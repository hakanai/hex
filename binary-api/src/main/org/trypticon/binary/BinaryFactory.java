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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Factory for creating binary implementations for common things.
 *
 * @author trejkaz
 */
public class BinaryFactory {

    /**
     * Wraps a byte buffer, returning it as a binary.
     *
     * @param buffer the byte buffer.
     * @return the binary.
     */
    public static Binary wrap(ByteBuffer buffer) {
        return new ByteBufferBinary(buffer);
    }

    /**
     * Wraps a byte array, returning it as a binary.
     *
     * @param array the byte array.
     * @return the binary.
     */
    public static Binary wrap(byte[] array) {
        return new ByteBufferBinary(ByteBuffer.wrap(array));
    }

    /**
     * Opens a file, returning it as a binary.
     *
     * @param file the file.
     * @return the binary.
     * @throws IOException if the file could not be opened for reading.
     */
    public static CloseableBinary open(File file) throws IOException {
        return new MemoryMappedFileBinary(file);
    }
}
