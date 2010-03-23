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
 * <p>A group of annotations.</p>
 *
 * <p>Groups cover a region of the binary file.  They can also be nested.</p>
 *
 * @author trejkaz
 */
public interface AnnotationGroup {

    /**
     * Gets the position in the binary of the first byte of the group.
     *
     * @return the position in the binary.
     */
    long getPosition();

    /**
     * Gets the length of the group.
     *
     * @return the length of the group.
     */
    int getLength();

    /**
     * A note added by the user.
     *
     * @return the note added by the user, {@code null} if there is no note.
     */
    String getNote();

}
