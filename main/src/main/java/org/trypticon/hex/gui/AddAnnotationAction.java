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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.OverlappingAnnotationException;
import org.trypticon.hex.anno.SimpleAnnotation;
import org.trypticon.hex.gui.anno.AddAnnotationPane;
import org.trypticon.hex.gui.anno.AnnotationOptionsPane;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.undo.AddEdit;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.interpreters.FixedLengthInterpreter;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.util.swingsupport.OptionPanes;

/**
 * Action to add an annotation.  Prompts the user for any options required and then
 * adds it.
 *
 * @author trejkaz
 */
class AddAnnotationAction extends NotebookPaneAction {
    private final AddAnnotationPane pane = new AddAnnotationPane();
    private final AnnotationOptionsPane optionsPane = new AnnotationOptionsPane();

    public AddAnnotationAction() {
        Resources.localiseAction(this, "AddAnnotation");
    }

    @Override
    protected void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception {
        InterpreterInfo info = pane.showDialog(notebookPane);
        if (info != null) {
            HexViewer viewer = notebookPane.getViewer();

            long position = viewer.getSelectionModel().getSelectionStart();

            List<InterpreterInfo.Option<?>> options = info.getOptions();
            Map<String, Object> optionMap = new HashMap<>(options.size());
            if (!options.isEmpty()) {
                optionsPane.displayOptions(options);
                if (OptionPanes.showInputDialog(notebookPane, optionsPane, optionsPane,
                                                Resources.getString("AddAnnotation.nameWithoutEllipsis"),
                                                Resources.getString("AddAnnotation.optionsOkButton"),
                                                UIManager.getString("OptionPane.cancelButtonText"))) {
                    optionsPane.populateOptionMap(optionMap);
                } else {
                    return;
                }
            }

            Interpreter interpreter = info.create(optionMap);

            long length;
            if (interpreter instanceof FixedLengthInterpreter) {
                length = ((FixedLengthInterpreter) interpreter).getValueLength();
            } else {
                length = (viewer.getSelectionModel().getSelectionEnd() -
                          viewer.getSelectionModel().getSelectionStart()) + 1;
            }

            Annotation annotation = new SimpleAnnotation(position, length, interpreter);
            AnnotationCollection annotationCollection = notebookPane.getNotebook().getAnnotations();

            try {
                notebookPane.getUndoHelper().perform(new AddEdit(annotationCollection, annotation));
            } catch (OverlappingAnnotationException e) {
                throw new ActionException(Resources.getMessage("AddAnnotation.Errors.overlap"), e);
            }
        }
    }
}
