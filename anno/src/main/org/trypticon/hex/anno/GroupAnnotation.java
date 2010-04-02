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

import java.util.List;

/**
 * <p>A sub-region of the binary file.</p>
 *
 * <p>Covers a region of the binary file, and can also be nested.</p>
 *
 * @author trejkaz
 */
public interface GroupAnnotation extends Annotation {

    /**
     * Gets the list of contained annotations.
     *
     * @return the list of contained annotations.
     */
    List<Annotation> getAnnotations();

    /**
     * Finds an annotation at the given position.
     *
     * @param position the position (relative to the entire file.)
     * @return the annotation found.
     */
    Annotation findAnnotationAt(long position);

}
