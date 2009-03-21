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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * Binary which maps a file into memory.
 *
 * @author trejkaz
 */
class MemoryMappedFileBinary extends AbstractBinary implements Binary, Closeable {

    /**
     * Delegate binary implementation.
     */
    private Binary delegate;

    /**
     * Cosntructs the binary, mapping the provided file into memory.
     *
     * @param file the file to map into memory.
     * @throws IOException if the file could not be read.
     */
    public MemoryMappedFileBinary(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            ByteBuffer mapped = stream.getChannel().map(MapMode.READ_ONLY, 0, file.length());
            delegate = new ByteBufferBinary(mapped);
        } finally {
            stream.close();
        }
    }

    public long length() {
        return delegate.length();
    }

    public byte read(long position) {
        return delegate.read(position);
    }

    public void read(long position, ByteBuffer buffer, int length) {
        delegate.read(position, buffer, length);
    }

    public void close() {
        delegate.close();
    }
}
