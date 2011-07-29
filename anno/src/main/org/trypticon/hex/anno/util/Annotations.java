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

package org.trypticon.hex.anno.util;

import org.trypticon.hex.anno.Annotation;

/**
 * Utilities for multiple annotation operations.
 *
 * @author trejkaz
 */
public class Annotations {

    /**
     * Tests if one annotation completely contains the region of another.
     *
     * @param outerAnnotation the outer annotation.
     * @param innerAnnotation the inner annotation.
     * @return {@code true} iff the outer annotation completely contains the inner annotation.
     */
    public static boolean contains(Annotation outerAnnotation, Annotation innerAnnotation) {
        long outerStart = outerAnnotation.getPosition();
        long outerEnd = outerStart + outerAnnotation.getLength() - 1;
        long innerStart = innerAnnotation.getPosition();
        long innerEnd = innerStart + innerAnnotation.getLength() - 1;

        // zero-length checks
        if (outerAnnotation.getLength() == 0) {
            //noinspection SimplifiableIfStatement
            if (innerAnnotation.getLength() == 0) {
                return outerStart == innerStart;
            } else {
                return false;
            }
        } else if (innerAnnotation.getLength() == 0) {
            return outerStart < innerStart && outerEnd >= innerStart;
        }

        return outerStart <= innerStart && outerEnd >= innerEnd;
    }

    /**
     * Tests if the covered region of the binary overlaps for the two annotations provided.
     *
     * @param annotation1 the first annotation.
     * @param annotation2 the second annotation.
     * @return {@code true} iff the two annotations overlap.
     */
    public static boolean overlap(Annotation annotation1, Annotation annotation2) {
        long start1 = annotation1.getPosition();
        long end1 = start1 + annotation1.getLength() - 1;
        long start2 = annotation2.getPosition();
        long end2 = start2 + annotation2.getLength() - 2;

        return start1 <= end2 && start2 <= end1;
    }
}
