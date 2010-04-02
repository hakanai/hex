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

import org.trypticon.hex.interpreters.nulls.NullInterpretor;
import org.trypticon.hex.anno.util.Annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of a single annotation group.
 *
 * @author trejkaz
 */
public class SimpleMutableGroupAnnotation extends SimpleMutableAnnotation implements GroupAnnotation {

    private final List<Annotation> annotations;

    public SimpleMutableGroupAnnotation(long position, int length, String note) {
        // TODO: Support interpretors for group annotations?  Or introduce a new level of hierarchy and
        //       have the Interpretor only on the leaf annotations?
        super(position, length, new NullInterpretor(), note);

        annotations = new ArrayList<Annotation>(4);
    }

    public SimpleMutableGroupAnnotation(long position, int length, String note, List<Annotation> annotations) {
        // TODO: Support interpretors for group annotations?  Or introduce a new level of hierarchy and
        //       have the Interpretor only on the leaf annotations?
        super(position, length, new NullInterpretor(), note);

        this.annotations = new ArrayList<Annotation>(annotations);
    }

    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public Annotation findAnnotationAt(long position) {
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

    public void add(Annotation annotation) throws OverlappingAnnotationException {
        int pos = Collections.binarySearch(annotations, annotation, new AnnotationPositionComparator());

        Annotation existingAnnotation;
        if (pos == -1) {
            existingAnnotation = null;
        } else if (pos >= 0) {
            existingAnnotation = annotations.get(pos);
        } else {
            existingAnnotation = annotations.get(-pos - 2);
        }

        if (existingAnnotation != null) {
            // 1. See if the new annotation is completely container inside the existing annotation.
            //   If it is, and the existing annotation is a group, then we can delegate down.

            if (Annotations.contains(existingAnnotation, annotation)) {
                if (existingAnnotation instanceof GroupAnnotation) {
                    ((SimpleMutableGroupAnnotation) existingAnnotation).add(annotation);
                    return;
                } else {
                    throw new OverlappingAnnotationException();
                }
            }

            // 2. See if the new annotation completely surrounds the existing annotation.

            if (Annotations.contains(annotation, existingAnnotation)) {
                throw new UnsupportedOperationException("Not supported yet, sorry.");
            }

            // 3. Any other kind of overlap is completely prohibited.

            if (Annotations.overlap(existingAnnotation, annotation)) {
                throw new OverlappingAnnotationException();
            }
        }

        // Otherwise there is no overlap so no problem.

        // If the location were found then one of the overlap checks above would have been satisfied,
        // so the position must be negative here.
        assert pos < 0;

        int insertionPoint = -pos - 1;
        annotations.add(insertionPoint, annotation);
    }

    public void remove(Annotation annotation) {
        Annotation foundAnnotation = findAnnotationAt(annotation.getPosition());

        if (foundAnnotation == null) {
            // No annotation at that position at all, let alone the one we wanted.
            throw new IllegalArgumentException("Annotation is not present so cannot be removed: " + annotation);
        }

        if (foundAnnotation.equals(annotation)) {
            annotations.remove(annotation);
        } else {
            // Found one but it wasn't the one we were looking for.
            // If it's a group annotation then we might find it further down the tree.
            if (foundAnnotation instanceof GroupAnnotation) {
                ((SimpleMutableGroupAnnotation) foundAnnotation).remove(annotation);
            } else {
                throw new IllegalArgumentException("Annotation is not present so cannot be removed: " + annotation);
            }
        }
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SimpleMutableGroupAnnotation)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!annotations.equals(((SimpleMutableGroupAnnotation) o).getAnnotations())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode = 71 * hashCode + annotations.hashCode();
        return hashCode;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString()).append(" {\n");
        for (Annotation child : getAnnotations()) {
            builder.append(child).append('\n');
        }
        return builder.append("}\n").toString();
    }
}
