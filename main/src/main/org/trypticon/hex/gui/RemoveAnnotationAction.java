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

package org.trypticon.hex.gui;

import java.awt.event.ActionEvent;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.util.swingsupport.ActionException;
import org.trypticon.hex.util.swingsupport.BaseAction;

/**
 * Action to remove an annotation
 *
 * @author trejkaz
 */
class RemoveAnnotationAction extends BaseAction {
    RemoveAnnotationAction() {
        putValue(NAME, "Remove Annotation");
    }

    protected void doAction(ActionEvent event) throws Exception {
        HexFrame frame = HexFrame.findActiveFrame();
        if (frame == null) {
            throw new ActionException("To remove an annotation, focus must be on the hex viewer.");
        }

        HexViewer viewer = frame.getNotebookPane().getViewer();

        long position = viewer.getSelectionModel().getCursor();

        Annotation annotation = viewer.getAnnotations().getAnnotationAt(position);
        if (annotation == null) {
            throw new ActionException("To remove an annotation, the cursor must be at an annotation.");
        }

        viewer.getAnnotations().remove(annotation);
    }
}
