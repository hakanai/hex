package org.trypticon.binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sun.nio.ch.DirectBuffer;

/**
 * <p>Extension on top of {@link ByteBufferBinary} which allows closing the buffer.</p>
 *
 * <p>This is normally extremely dangerous as closing a byte buffer means anyone holding onto the buffer can cause a JVM
 * crash.  Therefore we protect against this by (a) not giving this class to the outside world, and (b) checking a
 * closed flag on classes which use this one as a delegate.</p>
 *
 * @author trejkaz
 */
class CloseableByteBufferBinary extends ByteBufferBinary implements CloseableBinary {

    /**
     * Will be set to {@code true} on {@code close()}.
     */
    private volatile boolean closed = false;

    /**
     * A lock allowing multiple reading threads to get in at the same time, giving {@code close()} a way to determine
     * when all reads have finished so that it can set the {@code closed} flag and clean the buffer while knowing that a
     * read can't be half-completed.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CloseableByteBufferBinary(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    public byte read(long position) {
        lock.readLock().lock();
        try {
            throwIfClosed();
            return super.read(position);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void read(long position, ByteBuffer buffer, int length) {
        lock.readLock().lock();
        try {
            throwIfClosed();
            super.read(position, buffer, length);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void throwIfClosed() {
        if (closed) {
            throw new IllegalStateException("close() has already been called");
        }
    }

    public void close() throws IOException {
        lock.writeLock().lock();
        try {
            closed = true;
            if (buffer instanceof DirectBuffer) {
                ((DirectBuffer) buffer).cleaner().clean();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
