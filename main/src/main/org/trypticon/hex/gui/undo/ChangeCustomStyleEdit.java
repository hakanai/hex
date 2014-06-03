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

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.MutableAnnotationCollection;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.anno.ExtendedAnnotation;
import org.trypticon.hex.gui.anno.ParametricStyle;

/**
 * Undoable edit implementation for changing a custom style.
 *
 * @author trejkaz
 */
public class ChangeCustomStyleEdit implements DryUndoableEdit {
    private final AnnotationCollection annotationCollection;
    private final ExtendedAnnotation annotation;
    private final ParametricStyle oldCustomStyle;
    private final ParametricStyle newCustomStyle;

    /**
     * Constructs the edit.
     *
     * @param annotationCollection the annotation collection containing the annotation.
     * @param annotation the annotation to change.
     * @param oldCustomStyle the old custom style.
     * @param newCustomStyle the new custom style.
     */
    public ChangeCustomStyleEdit(MutableAnnotationCollection annotationCollection, ExtendedAnnotation annotation,
                                 ParametricStyle oldCustomStyle, ParametricStyle newCustomStyle) {
        this.annotationCollection = annotationCollection;
        this.annotation = annotation;
        this.oldCustomStyle = oldCustomStyle;
        this.newCustomStyle = newCustomStyle;
    }

    @Override
    public void execute() throws CannotRedoException {
        annotation.setCustomStyle(newCustomStyle);
    }

    @Override
    public void undo() throws CannotUndoException {
        annotation.setCustomStyle(oldCustomStyle);
    }

    @Override
    public String getPresentationName() {
        return Resources.getString("AnnotationViewer.Edits.changeCustomStyle");
    }
}
