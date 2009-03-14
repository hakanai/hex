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
 * Base class for new binary implementations.
 *
 * @author trejkaz
 */
public abstract class AbstractBinary implements Binary {

    public void read(long position, byte[] buffer) {
        ByteBuffer wrapped = ByteBuffer.wrap(buffer, 0, buffer.length);
        read(position, wrapped, buffer.length);
    }

    public void read(long position, byte[] buffer, int offset, int length) {
        ByteBuffer wrapped = ByteBuffer.wrap(buffer, offset, length);
        read(position, wrapped, length);
    }
}
