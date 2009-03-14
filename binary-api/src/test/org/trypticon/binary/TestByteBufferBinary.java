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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for {@link ByteBufferBinary}.
 *
 * @author trejkaz
 */
public class TestByteBufferBinary {

    @Test
    public void testReading() {
        byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Binary binary = new ByteBufferBinary(buffer);

        assertEquals("Wrong result", 0, binary.read(0));

        byte[] tmp = new byte[4];
        binary.read(1, tmp);
        assertArrayEquals("Wrong result", new byte[]{1, 2, 3, 4}, tmp);
        binary.read(5, tmp);
        assertArrayEquals("Wrong result", new byte[]{5, 6, 7, 8}, tmp);

        assertEquals("Wrong result", 9, binary.read(9));
    }
}
