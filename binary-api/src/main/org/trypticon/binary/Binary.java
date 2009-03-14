/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.binary;

import java.nio.ByteBuffer;

/**
 * Generic interface to things which contain binary data.
 *
 * @author trejkaz
 */
public interface Binary {

    /**
     * Gets the length of the binary, in bytes.
     *
     * @return the length of the binary, in bytes.
     */
    public long length();

    /**
     * Reads a single byte from a given position.
     *
     * @param position the position to read.
     * @return the single byte.
     */
    public byte read(long position);

    /**
     * Reads bytes from a given position into the buffer provided, filling the entire buffer.
     *
     * @param position tne position to begin reading from.
     * @param buffer the buffer to read into.
     * @param offset the offset in the buffer to begin reading into.
     * @param length the number of bytes to read.
     */
    public void read(long position, byte[] buffer, int offset, int length);

    /**
     * Reads bytes from a given position into the buffer provided, filling the entire buffer.
     *
     * @param position tne position to begin reading from.
     * @param buffer the buffer to read into.
     */
    public void read(long position, byte[] buffer);

    /**
     * Reads bytes from a given position into the buffer provided.
     *
     * @param position the position to begin reading from.
     * @param buffer the buffer to read into.
     * @param length the length to read.
     */
    public void read(long position, ByteBuffer buffer, int length);

}
