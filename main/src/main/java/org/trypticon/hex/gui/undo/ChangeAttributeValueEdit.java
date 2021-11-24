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

package org.trypticon.hex.gui.undo;

import javax.annotation.Nullable;
import javax.swing.undo.CannotUndoException;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.Attribute;
import org.trypticon.hex.gui.anno.ExtendedAnnotationCollection;

/**
 * Undoable edit implementation for changing an attribute on an annotation.
 *
 * @param <V> the type of the attribute value.
 * @author trejkaz
 */
public class ChangeAttributeValueEdit<V> implements DryUndoableEdit {
    private final ExtendedAnnotationCollection annotationCollection;
    private final Annotation annotation;
    private final Attribute<V> attribute;

    @Nullable
    private final V oldValue;

    @Nullable
    private final V newValue;

    private final String presentationName;

    /**
     * Constructs the edit.
     *
     * @param annotationCollection the annotation collection containing the annotation.
     * @param annotation the annotation to change.
     * @param attribute the attribute to change.
     * @param oldValue the old value.
     * @param newValue the new value.
     * @param presentationName a name to call the edit (e.g. "Change Note".)
     */
    public ChangeAttributeValueEdit(ExtendedAnnotationCollection annotationCollection,
                                    Annotation annotation, Attribute<V> attribute,
                                    @Nullable V oldValue, @Nullable V newValue,
                                    String presentationName) {
        this.annotationCollection = annotationCollection;
        this.annotation = annotation;
        this.attribute = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.presentationName = presentationName;
    }

    @Override
    public void execute() {
        annotationCollection.changeAttributeValue(annotation, attribute, newValue);
    }

    @Override
    public void undo() throws CannotUndoException {
        annotationCollection.changeAttributeValue(annotation, attribute, oldValue);
    }

    @Override
    public String getPresentationName() {
        return presentationName;
    }
}
