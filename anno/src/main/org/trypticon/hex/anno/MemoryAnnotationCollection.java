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

import java.util.*;

import org.trypticon.hex.anno.nulls.NullInterpretor;

/**
 * A collection of annotations kept in memory.
 *
 * @author trejkaz
 */
public class MemoryAnnotationCollection extends AbstractAnnotationCollection {
    private final GroupAnnotation rootGroup;

    public MemoryAnnotationCollection(long length) {
        // TODO: Support long.
        rootGroup = new SimpleMutableGroupAnnotation(0, (int) length, null);
    }

    public MemoryAnnotationCollection(GroupAnnotation rootGroup) {
        this.rootGroup = rootGroup;
    }

    public GroupAnnotation getRootGroup() {
        return rootGroup;
    }

    public List<Annotation> getTopLevel() {
        return rootGroup.getAnnotations();
    }

    public List<Annotation> getAnnotationPathAt(long position) {
        if (position < 0) {
            return null;
        }

        List<Annotation> path = null;
        Annotation annotation = rootGroup;

        while (annotation instanceof GroupAnnotation) {
            annotation = ((GroupAnnotation) annotation).findAnnotationAt(position);
            if (annotation == null) {
                break;
            }

            if (path == null) {
                path = new LinkedList<Annotation>();
            }
            path.add(annotation);
        }

        return path;
    }

    public void add(Annotation annotation) throws OverlappingAnnotationException {
        ((SimpleMutableGroupAnnotation) rootGroup).add(annotation);
        fireAnnotationsChanged();
    }

    public void remove(Annotation annotation) {
        ((SimpleMutableGroupAnnotation) rootGroup).remove(annotation);
        fireAnnotationsChanged();
    }
}
