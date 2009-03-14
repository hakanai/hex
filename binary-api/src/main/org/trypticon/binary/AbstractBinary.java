/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
