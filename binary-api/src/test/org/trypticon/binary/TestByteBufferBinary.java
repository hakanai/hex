/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
