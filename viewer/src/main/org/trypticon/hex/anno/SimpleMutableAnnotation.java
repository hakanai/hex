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

package org.trypticon.hex.anno;

/**
 * Implementation of a single annotation.
 *
 * @author trejkaz
 */
public class SimpleMutableAnnotation implements Annotation {
    private long position;
    private Interpretor interpretor;
    private String note;

    public SimpleMutableAnnotation(long position) {
        this(position, null, null);
    }

    public SimpleMutableAnnotation(long position, Interpretor interpretor, String note) {
        if (interpretor == null) {
            throw new IllegalArgumentException("interpretor cannot be null");
        }

        this.position = position;
        this.interpretor = interpretor;
        this.note = note;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public Interpretor getInterpretor() {
        return interpretor;
    }

    public void setInterpretor(Interpretor interpretor) {
        this.interpretor = interpretor;
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
               getInterpretor().equals(that.getInterpretor()) &&
               (getNote() == null ? that.getNote() == null : getNote().equals(that.getNote()));
    }

    @Override
    public int hashCode() {
        int hashCode = 23 * (int) getPosition();
        hashCode = hashCode * 37 + getInterpretor().hashCode();
        if (note != null) {
            hashCode = hashCode * 51 + getNote().hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (note != null) {
            return String.format("@%d:%s(%s)", position, interpretor, note);
        } else {
            return String.format("@%d:%s", position, interpretor);
        }
    }
}
