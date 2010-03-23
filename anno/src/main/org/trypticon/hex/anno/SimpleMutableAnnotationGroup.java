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

/**
 * Implementation of a single annotation group.
 *
 * @author trejkaz
 */
public class SimpleMutableAnnotationGroup implements AnnotationGroup {

    // In some respects a group is a bit like an annotation too.  The difference is that it doesn't have
    // an interpretor.
    private long position;
    private int length;
    private String note;

    public SimpleMutableAnnotationGroup(long position, int length, String note) {
        this.position = position;
        this.length = length;
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
        if (!(o instanceof SimpleMutableAnnotationGroup)) {
            return false;
        }
        SimpleMutableAnnotationGroup that = (SimpleMutableAnnotationGroup) o;
        return getPosition() == that.getPosition() &&
               getLength() == that.getLength() &&
               (getNote() == null ? that.getNote() == null : getNote().equals(that.getNote()));
    }

    @Override
    public int hashCode() {
        int hashCode = 51 * (int) getPosition();
        hashCode = hashCode * 91 + getLength();
        if (note != null) {
            hashCode = hashCode * 217 + getNote().hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        if (note != null) {
            return String.format("@%d(%s)", position, note);
        } else {
            return String.format("@%d", position);
        }
    }
}
