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

package org.trypticon.hex.gui.formats;

import java.awt.event.ActionEvent;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.gui.NotebookPaneAction;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.ActionException;

/**
 * Action which drops annotations for a structure at the selected position.
 *
 * @author trejkaz
 */
public class DropStructureAction extends NotebookPaneAction {

    private final Structure structure;

    public DropStructureAction(Structure structure) {
        this.structure = structure;

        putValue(NAME, Resources.getString("DropStructure.nameFormat", structure.getName()));
    }

    @Override
    protected void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception {
        HexViewer viewer = notebookPane.getViewer();

        Binary binary = viewer.getBinary();
        AnnotationCollection annotations = viewer.getAnnotations();
        long position = viewer.getSelectionModel().getSelectionStart();

        try {
            Annotation annotation = structure.drop(binary, position);
            annotations.add(annotation);
        } catch (Exception e) {
            throw new ActionException(Resources.getMessage("DropStructure.Errors.catchAll"), e);
        }
    }
}
