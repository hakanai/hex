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

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.trypticon.hex.anno.MutableAnnotation;
import org.trypticon.hex.anno.MutableAnnotationCollection;
import org.trypticon.hex.anno.OverlappingAnnotationException;
import org.trypticon.hex.gui.Resources;

/**
 * Undoable edit implementation for deleting an annotation.
 *
 * @author trejkaz
 */
public class DeleteEdit implements DryUndoableEdit {
    private final MutableAnnotationCollection annotationCollection;
    private final MutableAnnotation annotation;

    /**
     * Constructs the edit.
     *
     * @param annotationCollection the annotation collection we removed the annotation from.
     * @param annotation the annotation which was deleted.
     */
    public DeleteEdit(MutableAnnotationCollection annotationCollection, MutableAnnotation annotation) {
        this.annotationCollection = annotationCollection;
        this.annotation = annotation;
    }

    @Override
    public void execute() throws CannotRedoException {
        annotationCollection.remove(annotation);
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            annotationCollection.add(annotation);
        } catch (OverlappingAnnotationException e) {
            // Should be impossible.
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getPresentationName() {
        return Resources.getString("Delete.name");
    }
}
