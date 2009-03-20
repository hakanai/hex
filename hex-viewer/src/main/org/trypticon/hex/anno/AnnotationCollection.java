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
}
