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
