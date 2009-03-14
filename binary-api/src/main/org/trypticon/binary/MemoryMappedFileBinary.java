/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.binary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * Binary which maps a file into memory.
 *
 * @author trejkaz
 */
class MemoryMappedFileBinary extends AbstractBinary implements CloseableBinary {

    /**
     * Delegate binary implementation.
     */
    private CloseableBinary delegate;

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
            delegate = new CloseableByteBufferBinary(mapped);
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

    public void close() throws IOException {
        delegate.close();
    }
}
