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

package org.trypticon.hex.gui;

import java.awt.event.ActionEvent;
import java.util.List;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.undo.DeleteEdit;
import org.trypticon.hex.gui.util.ActionException;

/**
 * Action to remove an annotation
 *
 * @author trejkaz
 */
class DeleteAction extends NotebookPaneAction {
    DeleteAction() {
        Resources.localiseAction(this, "Delete");
    }

    @Override
    protected void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception {
        HexViewer viewer = notebookPane.getViewer();

        long position = viewer.getSelectionModel().getCursor();

        List<? extends Annotation> annotationPath = viewer.getAnnotations().getAnnotationPathAt(position);
        if (annotationPath == null) {
            throw new ActionException(Resources.getMessage("Delete.Errors.notAtAnnotation"));
        }

        // TODO: This results in a search but we already know the parent.  We could add a method which takes the parent.
        Annotation annotation = (Annotation) annotationPath.get(annotationPath.size() - 1);
        AnnotationCollection annotationCollection = notebookPane.getNotebook().getAnnotations();
        notebookPane.getUndoHelper().perform(new DeleteEdit(annotationCollection, annotation));
    }
}
