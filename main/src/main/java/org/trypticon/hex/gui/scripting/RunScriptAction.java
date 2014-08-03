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

package org.trypticon.hex.gui.scripting;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.OverlappingAnnotationException;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.formats.ruby.RubyStructureDSL;
import org.trypticon.hex.gui.NotebookPaneAction;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.undo.AddEdit;
import org.trypticon.hex.gui.util.ActionException;

/**
 * Action to run a script.
 *
 * @author trejkaz
 */
public class RunScriptAction extends NotebookPaneAction {
    private final Path scriptFile;

    public RunScriptAction(String fileName, Path scriptFile) {
        this.scriptFile = scriptFile;

        String scriptName = fileName;
        if (fileName.endsWith(".rb")) { // NON-NLS
            scriptName = fileName.substring(0, fileName.length() - 3);
        }
        putValue(NAME, scriptName);

        updateEnabled();
    }

    @Override
    protected void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception {
        Structure structure = RubyStructureDSL.loadFromFile(scriptFile);

        HexViewer viewer = notebookPane.getViewer();

        Binary binary = viewer.getBinary();
        AnnotationCollection annotationCollection = viewer.getAnnotations();
        long position = viewer.getSelectionModel().getSelectionStart();

        Annotation annotation;
        try {
            annotation = structure.drop(binary, position);
        } catch (Exception e) {
            throw new ActionException(Resources.getMessage("DropStructure.Errors.catchAll"), e);
        }

        try {
            notebookPane.getUndoHelper().perform(new AddEdit(annotationCollection, annotation));
        } catch (OverlappingAnnotationException e) {
            throw new ActionException(Resources.getMessage("AddAnnotation.Errors.overlap"), e);
        }
    }
}
