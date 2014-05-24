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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookFileFilter;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.gui.util.BaseAction;
import org.trypticon.hex.util.swingsupport.ImprovedFileChooser;

/**
 * Action to save the notebook.
 *
 * @author trejkaz
 */
public class SaveNotebookAction extends BaseAction {
    private final PreferredDirectoryManager preferredDirectoryManager;
    private final boolean alwaysAsk;

    SaveNotebookAction(PreferredDirectoryManager preferredDirectoryManager, boolean alwaysAsk) {
        this.preferredDirectoryManager = preferredDirectoryManager;
        this.alwaysAsk = alwaysAsk;

        Resources.localiseAction(this, alwaysAsk ? "SaveAs" : "Save");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        doSave();
    }

    private void doSave() throws Exception {
        HexFrame frame = HexFrame.findActiveFrame();
        if (frame == null || frame.getNotebook() == null) {
            throw new ActionException(Resources.getMessage("Save.Errors.notFocused"));
        }

        Notebook notebook = frame.getNotebook();

        URL location;
        if (!alwaysAsk && notebook.getNotebookLocation() != null) {
            location = notebook.getNotebookLocation();
        } else {
            JFileChooser chooser = new ImprovedFileChooser();
            chooser.setFileFilter(new NotebookFileFilter());

            chooser.setCurrentDirectory(preferredDirectoryManager.getPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS));

            while (true) {
                File chosenFile;
                if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    chosenFile = chooser.getSelectedFile();
                    if (chosenFile.exists()) {
                        if (JOptionPane.showConfirmDialog(frame,
                                                          Resources.getString("Save.confirmOverwrite"),
                                                          Resources.getString("Save.name"),
                                                          JOptionPane.YES_NO_OPTION,
                                                          JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                            continue;
                        }
                    }

                    preferredDirectoryManager.setPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS, chooser.getCurrentDirectory());

                    location = chosenFile.toURI().toURL();
                    break;
                } else {
                    return;
                }
            }
        }

        new NotebookStorage().write(notebook, location);

        notebook.setNotebookLocation(location);
    }

    /**
     * Saves the document programmatically, showing user interface as required.
     * If errors occur, UI is shown for these and {@code false} is returned.
     *
     * @param owner the owner to use in the event that dialogs need to be displayed.
     * @return {@code true} if the save was successful.
     */
    public boolean save(Component owner) {
        try {
            doSave();
            return true;
        } catch (Throwable t) {
            handleError(owner, t);
            return false;
        }
    }
}
