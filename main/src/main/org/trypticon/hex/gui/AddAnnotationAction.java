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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.OverlappingAnnotationException;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.gui.anno.AddAnnotationPane;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.undo.AddEdit;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.interpreters.FixedLengthInterpreter;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterInfo;

/**
 * Action to add an annotation.  Prompts the user for any options required and then
 * adds it.
 *
 * @author trejkaz
 */
class AddAnnotationAction extends NotebookPaneAction {
    private final AddAnnotationPane pane = new AddAnnotationPane();

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

                // TODO: We should do this in a single form, I'm just lazy right now.

                for (InterpreterInfo.Option option : options) {
                    while (true) {
                        String requiredOrOptional = option.isRequired() ?
                                                    Resources.getString("AddAnnotation.enterValueForParameter.required") :
                                                    Resources.getString("AddAnnotation.enterValueForParameter.optional");
                        String message = Resources.getString("AddAnnotation.enterValueForParameter",
                                                             option.getName(),
                                                             requiredOrOptional);
                        String value = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(viewer), message);

                        if (value == null) {
                            return; // Cancelled.
                        }

                        value = value.trim();
                        if (!value.isEmpty()) {

                            // TODO: Support more types in a more generic way.  Forms will help.

                            if (String.class == option.getType()) {
                                optionMap.put(option.getName(), value);
                                break;
                            } else if (Integer.class == option.getType()) {
                                optionMap.put(option.getName(), Integer.valueOf(value));
                                break; // the while loop
                            }
                        } else {
                            if (!option.isRequired()) {
                                break;
                            }
                        }
                    }
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

            SimpleMutableAnnotation annotation = new SimpleMutableAnnotation(position, length, interpreter, null);
            AnnotationCollection annotationCollection = viewer.getAnnotations();

            try {
                annotationCollection.add(annotation);
            } catch (OverlappingAnnotationException e) {
                throw new ActionException(Resources.getMessage("AddAnnotation.Errors.overlap"), e);
            }
            
            notebookPane.addEdit(new AddEdit(annotationCollection, annotation));
        }

//
    }
}
