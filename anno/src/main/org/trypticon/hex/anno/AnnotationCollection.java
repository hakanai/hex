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

import java.util.List;

/**
 * A collection of annotations.
 *
 * @author trejkaz
 */
public interface AnnotationCollection {
    /**
     * Gets a list of all annotations.
     *
     * @return the list of all annotations.
     */
    List<Annotation> getAll();

    /**
     * <p>Gets the annotation at the given position, if any.</p>
     *
     * <p>This is currently limited to a single result, and it is up to the collection
     *    to decide which to return in the case of two residing at the same location.</p>
     *
     * @param position the position to look up.
     * @return the annotation at that position, or {@code null} if there are none.
     */
    Annotation getAnnotationAt(long position);

    /**
     * Adds an annotation.
     *
     * @param annotation the annotation to add.
     */
    void add(Annotation annotation);

    /**
     * Removes an annotation.
     *
     * @param annotation the annotation to remove.
     */
    void remove(Annotation annotation);

    /**
     * Adds a listener for changes in the collection.
     *
     * @param listener the listener to add.
     */
    void addAnnotationCollectionListener(AnnotationCollectionListener listener);

    /**
     * Removes a listener for changes in the collection.
     *
     * @param listener the listener to remove.
     */
    void removeAnnotationCollectionListener(AnnotationCollectionListener listener);
}
