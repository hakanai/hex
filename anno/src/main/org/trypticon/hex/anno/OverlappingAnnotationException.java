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
 * Thrown when trying to add an {@link Annotation} to an {@link AnnotationCollection}, but
 * it would cross over some other annotation.
 */
public class OverlappingAnnotationException extends Exception {

    private final Annotation existing;
    private final Annotation attempted;

    @Deprecated
    public OverlappingAnnotationException() {
        this.existing = null;
        this.attempted = null;
    }

    public OverlappingAnnotationException(Annotation existing, Annotation attempted) {
        super(String.format("Overlapping annotations.\n  attempted to add: %s\n  would have overlapped: %s",
                            attempted, existing));
        this.existing = existing;
        this.attempted = attempted;
    }

    // TODO: Reconstruct message after deserialisation
}
