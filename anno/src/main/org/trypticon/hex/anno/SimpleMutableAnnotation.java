/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

package org.trypticon.hex.anno;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.Value;

/**
 * Implementation of a single annotation.
 *
 * @author trejkaz
 */
public class SimpleMutableAnnotation implements MutableAnnotation {
    private long position;
    private int length;
    private Interpreter interpreter;
    private String note;

    public SimpleMutableAnnotation(long position, int length, Interpreter interpreter, String note) {
        if (interpreter == null) {
            throw new IllegalArgumentException("interpreter cannot be null");
        }

        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }

        if (length < 0) {
            throw new IllegalArgumentException("length cannot be negative");
        }

        this.position = position;
        this.length = length;
        this.interpreter = interpreter;
        this.note = note;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Value interpret(Binary binary) {
        return interpreter.interpret(binary, position, length);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SimpleMutableAnnotation)) {
            return false;
        }
        SimpleMutableAnnotation that = (SimpleMutableAnnotation) o;
        return getPosition() == that.getPosition() &&
               getLength() == that.getLength() &&
               getInterpreter().equals(that.getInterpreter()) &&
               (getNote() == null ? that.getNote() == null : getNote().equals(that.getNote()));
    }

    @Override
    public int hashCode() {
        int hashCode = 23 * (int) getPosition();
        hashCode = hashCode * 71 + getLength();
        hashCode = hashCode * 37 + getInterpreter().hashCode();
        if (note != null) {
            hashCode = hashCode * 51 + getNote().hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (note != null) {
            return String.format("@%d:%s(%s)", position, interpreter, note);
        } else {
            return String.format("@%d:%s", position, interpreter);
        }
    }
}
