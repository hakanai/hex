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
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.interpreters.nulls.NullInterpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A utility to search within a list of annotations.
 */
public class AnnotationRangeSearcher {

    private final Comparator<Annotation> comp = new AnnotationPositionComparator();

    /**
     * Finds all annotations which cross the specified range.
     *
     * @param list the list of annotations to search.
     * @param annotation an annotation marking the range being searched.
     * @return the list of annotations crossing that range.  Returns an empty list if there are no hits.
     */
    public List<AnnotationRangeSearchHit> findAllInRange(List<Annotation> list, Annotation annotation) {
        List<AnnotationRangeSearchHit> results = new ArrayList<AnnotationRangeSearchHit>(10);

        int startMatch = binaryPositionSearch(list, annotation.getPosition());

        // If startMatch is greater than or equal to zero then we have an exact hit so the annotation will be
        // completely inside the specified range.
        // Otherwise we need to look at the annotation one before the insertion point as it might cross us.
        if (startMatch < 0) {
            if (startMatch == -1) {
                // In this situation there was no annotation before our position either.
            } else {
                Annotation precedingAnnotation = list.get(-startMatch - 2);
                if (Annotations.contains(precedingAnnotation, annotation)) {
                    results.add(new AnnotationRangeSearchHit(precedingAnnotation, AnnotationRangeSearchHit.Relation.SURROUNDING));
                    return results;
                } else if (Annotations.overlap(precedingAnnotation, annotation)) {
                    // Partial overlap.
                    results.add(new AnnotationRangeSearchHit(precedingAnnotation, AnnotationRangeSearchHit.Relation.INTERSECTING_START));
                } else {
                    // No overlap at all, thus not a result.
                }
            }

            // Either way the insertion point becomes the start index for the annotations which are entirely inside the range.
            startMatch = -startMatch - 1;
        } else {
            // If it starts at the same point it might still be surrounding, or might be the same range.
            Annotation firstAnnotation = list.get(startMatch);
            if (firstAnnotation.getLength() == annotation.getLength()) {
                results.add(new AnnotationRangeSearchHit(firstAnnotation, AnnotationRangeSearchHit.Relation.SAME_RANGE));
                return results;
            } else if (Annotations.contains(firstAnnotation, annotation)) {
                results.add(new AnnotationRangeSearchHit(firstAnnotation, AnnotationRangeSearchHit.Relation.SURROUNDING));
                return results;
            } else {
                // Otherwise it's inside, and will be handled below.
            }
        }

        int endMatch = binaryPositionSearch(list, annotation.getPosition() + annotation.getLength() - 1);

        if (endMatch < 0) {
            // Again in this situation we want to look at the one before the insertion point.
            endMatch = -endMatch - 2;
        }

        if (startMatch <= endMatch) {
            Annotation lastAnnotation = list.get(endMatch);

            if (Annotations.contains(annotation, lastAnnotation)) {
                for (int i = startMatch; i <= endMatch; i++) {
                    results.add(new AnnotationRangeSearchHit(list.get(i), AnnotationRangeSearchHit.Relation.CONTAINED_WITHIN));
                }
            } else {
                for (int i = startMatch; i < endMatch; i++) { // note that this is intentionally < when the one above is <=
                    results.add(new AnnotationRangeSearchHit(list.get(i), AnnotationRangeSearchHit.Relation.CONTAINED_WITHIN));
                }

                results.add(new AnnotationRangeSearchHit(lastAnnotation, AnnotationRangeSearchHit.Relation.INTERSECTING_END));
            }
        }

        return results;
    }

    /**
     * Performs a binary search through the list by position.
     *
     * @param list the list.
     * @param position the position.
     * @return a binary search result.  If the result is non-negative, then it represents the index of an
     *         annotation with the position specified.  If the result is negative, then it represents the
     *         point at which a new element would be inserted to maintain the list ordering.
     */
    private int binaryPositionSearch(List<Annotation> list, long position) {
        Annotation template = new SimpleMutableAnnotation(position, 1, new NullInterpreter(), null);
        return Collections.binarySearch(list, template, comp);
    }
}
