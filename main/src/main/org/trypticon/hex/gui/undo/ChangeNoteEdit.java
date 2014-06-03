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

import javax.swing.undo.CannotUndoException;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.anno.ExtendedAnnotation;
import org.trypticon.hex.gui.anno.ExtendedAnnotationCollection;

/**
 * Undoable edit implementation for changing a note.
 *
 * @author trejkaz
 */
public class ChangeNoteEdit implements DryUndoableEdit {
    private final ExtendedAnnotationCollection annotationCollection;
    private final ExtendedAnnotation annotation;
    private final String oldNote;
    private final String newNote;

    /**
     * Constructs the edit.
     *
     * @param annotationCollection the annotation collection containing the annotation.
     * @param annotation the annotation to change.
     * @param oldNote the old note.
     * @param newNote the new note.
     */
    public ChangeNoteEdit(ExtendedAnnotationCollection annotationCollection, ExtendedAnnotation annotation,
                          String oldNote, String newNote) {
        this.annotationCollection = annotationCollection;
        this.annotation = annotation;
        this.oldNote = oldNote;
        this.newNote = newNote;
    }

    @Override
    public void execute() {
        annotationCollection.changeNote(annotation, newNote);
    }

    @Override
    public void undo() throws CannotUndoException {
        annotationCollection.changeNote(annotation, oldNote);
    }

    @Override
    public String getPresentationName() {
        return Resources.getString("AnnotationViewer.Edits.changeNote");
    }
}
