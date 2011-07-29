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

import org.trypticon.hex.anno.util.AnnotationPositionComparator;
import org.trypticon.hex.anno.util.AnnotationRangeSearchHit;
import org.trypticon.hex.anno.util.AnnotationRangeSearcher;
import org.trypticon.hex.interpreters.nulls.NullInterpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of a single annotation group.
 *
 * @author trejkaz
 */
public class SimpleMutableGroupAnnotation extends SimpleMutableAnnotation implements MutableGroupAnnotation {

    private final List<Annotation> annotations;

    public SimpleMutableGroupAnnotation(long position, int length, String note) {
        // TODO: Support interpreters for group annotations?  Or introduce a new level of hierarchy and
        //       have the Interpreter only on the leaf annotations?
        super(position, length, new NullInterpreter(), note);

        annotations = new ArrayList<Annotation>(4);
    }

    public SimpleMutableGroupAnnotation(long position, int length, String note, List<Annotation> annotations) {
        // TODO: Support interpreters for group annotations?  Or introduce a new level of hierarchy and
        //       have the Interpreter only on the leaf annotations?
        super(position, length, new NullInterpreter(), note);

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

    public GroupAnnotation findDeepestGroupAnnotationAt(long position) {
        Annotation annotation = findAnnotationAt(position);
        if (!(annotation instanceof GroupAnnotation)) {
            return null;
        }

        GroupAnnotation groupAnnotation = (GroupAnnotation) annotation;
        GroupAnnotation deepest = groupAnnotation.findDeepestGroupAnnotationAt(position);
        if (deepest == null) {
            return groupAnnotation;
        } else {
            return deepest;
        }
    }

    public void add(Annotation annotation) throws OverlappingAnnotationException {
        List<AnnotationRangeSearchHit> hits = new AnnotationRangeSearcher().findAllInRange(annotations, annotation);
        if (hits.size() == 0) {
            // No annotations in the vicinity at all, just add it and bail.
            insertInPosition(annotation);
            return;
        }

        if (hits.get(0).getRelation() == AnnotationRangeSearchHit.Relation.INTERSECTING_START) {
            throw new OverlappingAnnotationException(hits.get(0).getAnnotation(), annotation);
        }

        if (hits.get(hits.size() - 1).getRelation() == AnnotationRangeSearchHit.Relation.INTERSECTING_END) {
            throw new OverlappingAnnotationException(hits.get(hits.size() - 1).getAnnotation(), annotation);
        }

        // Dealing with surrounding is simple.  If it was a group then we recurse to add inside the group,
        // otherwise it's illegal.
        if (hits.get(0).getRelation() == AnnotationRangeSearchHit.Relation.SURROUNDING) {
            if (hits.get(0).getAnnotation() instanceof GroupAnnotation) {
                // No problem, the new annotation will go into that group.
                ((SimpleMutableGroupAnnotation) hits.get(0).getAnnotation()).add(annotation);
                return;
            } else {
                throw new OverlappingAnnotationException(hits.get(0).getAnnotation(), annotation);
            }
        }

        // For the same range, the order we nest will depend on which one is a group vs. a leaf.
        if (hits.get(0).getRelation() == AnnotationRangeSearchHit.Relation.SAME_RANGE) {
            if (hits.get(0).getAnnotation() instanceof GroupAnnotation) {
                // The case of annotation also being a GroupAnnotation is ambiguous in that we could nest
                // them either way.  But we'll just treat the new one as inside the old one, which is simpler.
                ((SimpleMutableGroupAnnotation) hits.get(0).getAnnotation()).add(annotation);
                return;
            } else {
                // Otherwise we treat it the same as CONTAINED_WITHIN which is handled below.
            }
        }

        // Now the hits are entirely contained within the range.  As was the case with the surrounding case,
        // this is only legal if the one containing the others is a group.
        if (annotation instanceof GroupAnnotation) {
            SimpleMutableGroupAnnotation group = (SimpleMutableGroupAnnotation) annotation;

            // Move the contained annotations inside the group.  This should succeed unless the caller does
            // something dumb like putting some annotations inside the group.  If it fails, at least the
            // subsequent calls will not be made, so things should still be consistent.
            for (AnnotationRangeSearchHit hit : hits) {
                group.add(hit.getAnnotation());
            }

            // Now remove them from ourselves.
            for (AnnotationRangeSearchHit hit : hits) {
                remove(hit.getAnnotation());
            }

            // And finally add the group to ourselves.  We know this must be safe because we just removed all the
            // annotations in its location.
            insertInPosition(annotation);

        } else {
            throw new OverlappingAnnotationException(hits.get(0).getAnnotation(), annotation); // picks the first one
        }
    }

    /**
     * Inserts an annotation into the list in the correct order.  At the time this is called, all the necessary
     * sanity checks should already have been performed.
     *
     * @param annotation the annotation to add.
     */
    private void insertInPosition(Annotation annotation) {
        int pos = binaryPositionSearch(annotation.getPosition());
        if (pos < 0) {
            pos = -pos - 1;
        }
        annotations.add(pos, annotation);

    }

    public void remove(Annotation annotation) {
        Annotation foundAnnotation = findAnnotationAt(annotation.getPosition());

        if (foundAnnotation == null) {
            // No annotation at that position at all, let alone the one we wanted.
            throw new IllegalArgumentException("Annotation is not present so cannot be removed: " + annotation);
        }

        if (foundAnnotation.equals(annotation)) {
            annotations.remove(annotation);

            // We removed a group so we have to add its children back.
            if (annotation instanceof GroupAnnotation) {
                for (Annotation childAnnotation : ((GroupAnnotation) annotation).getAnnotations()) {
                    try {
                        add(childAnnotation);
                    } catch (OverlappingAnnotationException e) {
                        throw new IllegalStateException("Got an overlap - should be impossible", e);
                    }
                }
            }
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
        Annotation template = new SimpleMutableAnnotation(position, 1, new NullInterpreter(), null);
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString()).append(" {\n");
        for (Annotation child : getAnnotations()) {
            builder.append(child).append('\n');
        }
        return builder.append("}\n").toString();
    }
}
