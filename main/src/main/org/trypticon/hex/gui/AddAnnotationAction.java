/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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
import org.trypticon.hex.anno.FixedLengthInterpretor;
import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.anno.InterpretorInfo;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.util.swingsupport.ActionException;
import org.trypticon.hex.util.swingsupport.BaseAction;

/**
 * Action to add an annotation.  Prompts the user for any options required and then
 * adds it.
 *
 * @author trejkaz
 */
class AddAnnotationAction extends BaseAction {
    private final InterpretorInfo info;

    public AddAnnotationAction(InterpretorInfo info) {
        this.info = info;

        putValue(NAME, info.getHumanName());
    }

    protected void doAction(ActionEvent event) throws Exception {
        HexFrame frame = HexFrame.findActiveFrame();
        if (frame == null || frame.getNotebookPane() == null) {
            throw new ActionException("To add an annotation, focus must be on the hex viewer.");
        }

        HexViewer viewer = frame.getNotebookPane().getViewer();

        long position = viewer.getSelectionModel().getSelectionStart();

        List<InterpretorInfo.Option> options = info.getOptions();
        Map<String, Object> optionMap = new HashMap<String, Object>(options.size());
        if (!options.isEmpty()) {

            // TODO: We should do this in a single form, I'm just lazy right now.

            for (InterpretorInfo.Option option : options) {
                while (true) {
                    String value = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(viewer),
                                                               "Enter a value for parameter: " + option.getName() + " (" +
                                                               (option.isRequired() ? "required" : "optional") + ")");

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

        Interpretor interpretor = info.create(optionMap);

        int length;
        if (interpretor instanceof FixedLengthInterpretor) {
            length = ((FixedLengthInterpretor) interpretor).getValueLength();
        } else {
            length = (int) (viewer.getSelectionModel().getSelectionEnd() -
                            viewer.getSelectionModel().getSelectionStart()) + 1;
        }

        SimpleMutableAnnotation annotation = new SimpleMutableAnnotation(position, length, interpretor, null);

        viewer.getAnnotations().add(annotation);
    }
}
