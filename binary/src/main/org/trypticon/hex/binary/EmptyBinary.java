package org.trypticon.hex.binary;

import java.nio.ByteBuffer;

/**
 * A binary with 0 bytes of content.
 */
public class EmptyBinary extends AbstractBinary {
    public long length() {
        return 0;
    }

    public byte read(long position) {
        throw new IndexOutOfBoundsException("Binary has no data");
    }

    public void read(long position, ByteBuffer buffer, int length) {
        if (length < 0) {
            throw new IndexOutOfBoundsException("Length cannot be negative");
        }
        if (length > 0) {
            throw new IndexOutOfBoundsException("Binary has no data");
        }
    }
}
