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
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

import org.trypticon.hex.anno.nulls.NullInterpretor;

/**
 * A collection of annotations kept in memory.
 *
 * @author trejkaz
 */
public class MemoryAnnotationCollection extends AbstractAnnotationCollection {
    private final List<Annotation> annotations;
    private final List<AnnotationGroup> groups;

    public MemoryAnnotationCollection() {
        annotations = new ArrayList<Annotation>(50);
        groups = new ArrayList<AnnotationGroup>(10);
    }

    public MemoryAnnotationCollection(List<Annotation> annotations, List<AnnotationGroup> groups) {
        this.annotations = new ArrayList<Annotation>(annotations);

        if (groups == null) {
            // Supports earlier files which didn't have groups in them.
            this.groups = new ArrayList<AnnotationGroup>(10);
        } else {
            this.groups = new ArrayList<AnnotationGroup>(groups);
        }
    }

    public List<Annotation> getAll() {
        return Collections.unmodifiableList(annotations);
    }

    public List<Annotation> findAnnotationsWithin(long position, int length) {
        int startIndexInclusive = binaryPositionSearch(position);
        if (startIndexInclusive < 0)
        {
            startIndexInclusive = -startIndexInclusive - 1;
        }

        int endIndexExclusive = binaryPositionSearch(position + length - 1);
        if (endIndexExclusive < 0)
        {
            endIndexExclusive = -endIndexExclusive - 1;
        }

        return annotations.subList(startIndexInclusive, endIndexExclusive);
    }

    public Annotation getAnnotationAt(long position) {
        if (position < 0) {
            return null;
        }

        int pos = binaryPositionSearch(position);
        if (pos >= 0) {
            // Direct hit on the first position for an annotation.
            return annotations.get(pos);
        } else {
            // Find the nearest to the left.
            // -pos - 1 is the insertion point, so -pos - 2 would be the annotation before it.
            pos = -pos - 2;
            if (pos == -1) {
                // No annotations to the left, so impossible for one to cross the position we searched for.
                return null;
            }

            Annotation annotation = annotations.get(pos);

            // If it ends at the position passed in, or some point after it, then it's a match.
            long annotationEndPosition = annotation.getPosition() + annotation.getLength() - 1;
            if (annotationEndPosition >= position) {
                return annotation;
            } else {
                return null;
            }
        }
    }

    public List<AnnotationGroup> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public void add(AnnotationGroup group) {
        // TODO: Prohibit if it will cross an annotation - annotations wholly inside are OK though.
        // TODO: Prohibit if it will cross an existing annotation group.
        // TODO: Needs to be in sorted order.
        groups.add(group);
    }

    public void remove(AnnotationGroup group) {
        groups.remove(group);
    }

    /**
     * Finds an annotation which crosses the position specified.
     *
     * @param position the position.
     * @return the index of an annotation which intersects the position.  If no annotations intersect the given
     *         position, then a negative value is returned where the insertion point can be determined by negating the
     *         result and subtracting one.
     */
    private int binaryPositionSearch(long position)
    {
        Annotation template = new SimpleMutableAnnotation(position, 1, new NullInterpretor(), null);
        return Collections.binarySearch(annotations, template, new AnnotationPositionComparator());
    }

    public void add(Annotation annotation) {
        // TODO: Prohibit if it will cross an existing annotation.
        int pos = Collections.binarySearch(annotations, annotation, new AnnotationPositionComparator());
        if (pos < 0) {
            pos = -pos - 1;
        }
        annotations.add(pos, annotation);
        fireAnnotationsChanged();
    }

    public void remove(Annotation annotation) {
        annotations.remove(annotation);
        fireAnnotationsChanged();
    }

    private class AnnotationPositionComparator implements Comparator<Annotation> {
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
}
