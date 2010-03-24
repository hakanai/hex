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

package org.trypticon.hex.anno.strings;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.trypticon.hex.anno.AbstractInterpretor;
import org.trypticon.hex.binary.Binary;

/**
 * Interpretor for string values.
 *
 * @author trejkaz
 */
public class StringInterpretor extends AbstractInterpretor<StringValue> {
    private final Charset charset;

    public StringInterpretor(String charset) {
        super(StringValue.class);
        this.charset = Charset.forName(charset);
    }

    public String getCharset() {
        return charset.name();
    }

    public StringValue interpret(Binary binary, long position, int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        binary.read(position, buffer, length);
        buffer.rewind();

        CharBuffer charBuffer = charset.decode(buffer);
        return new SimpleStringValue(charBuffer.toString(), length);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StringInterpretor && charset.equals(((StringInterpretor) o).charset);
    }

    @Override
    public int hashCode() {
        return 234611 ^ charset.hashCode();
    }

    @Override
    public String toString() {
        return String.format("string(%s)", charset.name());
    }
}
