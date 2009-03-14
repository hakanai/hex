/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.binary;

import java.nio.ByteBuffer;

/**
 * Binary which wraps a byte buffer.
 *
 * @author trejkaz
 */
class ByteBufferBinary extends AbstractBinary {

    /**
     * The wrapped byte buffer.
     */
    final ByteBuffer buffer;

    /**
     * Constructs binary wrapping a byte buffer.
     *
     * @param buffer the wrapped byte buffer.
     */
    public ByteBufferBinary(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public long length() {
        return buffer.capacity();
    }

    public byte read(long position) {
        return buffer.get((int) position);
    }

    public void read(long position, ByteBuffer buffer, int length) {
        ByteBuffer dup = this.buffer.duplicate();
        dup.position((int) position);
        dup.limit(length + (int) position);
        buffer.put(dup);
    }
}
