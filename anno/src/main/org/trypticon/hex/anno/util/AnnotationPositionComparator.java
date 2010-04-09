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

import java.util.Comparator;

/**
 * Comparator which orders annotations by their position.
 */
public class AnnotationPositionComparator implements Comparator<Annotation> {
    public int compare(Annotation annotation1, Annotation annotation2) {
        if (annotation1.getPosition() < annotation2.getPosition()) {
            return -1;
        } else if (annotation1.getPosition() > annotation2.getPosition()){
            return 1;
        } else {
            return 0;
        }
    }
}
