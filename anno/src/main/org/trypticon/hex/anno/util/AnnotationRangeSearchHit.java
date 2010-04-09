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
 * Holds a single hit returned from {@link AnnotationRangeSearcher}
 */
public class AnnotationRangeSearchHit {

    private final Annotation annotation;
    private final Relation relation;

    public AnnotationRangeSearchHit(Annotation annotation, Relation relation) {
        this.annotation = annotation;
        this.relation = relation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    public String toString() {
        return relation.toString() + ":" + annotation.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnnotationRangeSearchHit)) {
            return false;
        }
        AnnotationRangeSearchHit hit = (AnnotationRangeSearchHit) o;
        return hit.annotation.equals(annotation) && hit.relation == relation;
    }

    @Override
    public int hashCode() {
        return 105 * annotation.hashCode() + relation.hashCode();
    }

    /**
     * Enumerates the possible relationships for a hit.
     */
    public static enum Relation {
        /**
         * The annotation found covers exactly the same range as the search range.
         */
        SAME_RANGE,

        /**
         * The annotation found is entirely inside the search range.
         */
        CONTAINED_WITHIN,

        /**
         * The annotation found is entirely surrounding the search range.
         */
        SURROUNDING,

        /**
         * The annotation found intersects the start of the search range.
         */
        INTERSECTING_START,

        /**
         * The annotation found intersects the end of the search range.
         */
        INTERSECTING_END,
    }
}
