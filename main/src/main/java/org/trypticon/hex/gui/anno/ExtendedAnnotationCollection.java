/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
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

package org.trypticon.hex.gui.anno;

import java.util.Objects;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.Attribute;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.anno.SimpleGroupAnnotation;

/**
 * Extensions to the basic annotation collection.
 *
 * @author trejkaz
 */
public class ExtendedAnnotationCollection extends MemoryAnnotationCollection {
    public ExtendedAnnotationCollection(long length) {
        super(new SimpleGroupAnnotation(0, length));
    }

    public ExtendedAnnotationCollection(GroupAnnotation rootGroup) {
        super(rootGroup);
    }

    /**
     * Changes an attribute on an annotation and fires an event about it.
     *
     * @param annotation the annotation.
     * @param attribute the attribute to change.
     * @param newValue the new value for the attribute.
     * @param <V> the type of the attribute.
     */
    public <V> void changeAttributeValue(Annotation annotation, Attribute<V> attribute, V newValue) {
        V oldValue = annotation.get(attribute);
        annotation.set(attribute, newValue);
        if (!Objects.equals(oldValue, newValue)) {
            fireAnnotationChanged(annotation);
        }
    }
}
