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
