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
