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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import javax.swing.JOptionPane;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookFileFilter;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.gui.util.FileExtensionFilter;
import org.trypticon.hex.gui.util.FileSelection;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Action to save the notebook.
 *
 * @author trejkaz
 */
public class SaveNotebookAction extends NotebookPaneAction {
    private final PreferredDirectoryManager preferredDirectoryManager;
    private final boolean alwaysAsk;

    SaveNotebookAction(PreferredDirectoryManager preferredDirectoryManager, boolean alwaysAsk) {
        this.preferredDirectoryManager = preferredDirectoryManager;
        this.alwaysAsk = alwaysAsk;

        Resources.localiseAction(this, alwaysAsk ? "SaveAs" : "Save");
    }

    @Override
    protected void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception {
        doSave();
    }

    private void doSave() throws Exception {
        HexFrame frame = HexFrame.findActiveFrame();
        Notebook notebook = frame.getNotebook();

        URL location;
        if (!alwaysAsk && notebook.getNotebookLocation() != null) {
            location = notebook.getNotebookLocation();
        } else {
            FileSelection fileSelection = FileSelection.getInstance();
            File directory = preferredDirectoryManager.getPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS);
            FileExtensionFilter filter = new NotebookFileFilter();

            while (true) {
                File chosenFile = fileSelection.selectFile(frame, FileSelection.Mode.SAVE, directory, filter);
                if (chosenFile != null) {
                    // So that next iteration defaults to the right location.
                    directory = chosenFile.getParentFile();

                    // Aqua already confirms this for us.
                    if (chosenFile.exists() && !PLAFUtils.isAqua()) {
                        if (JOptionPane.showConfirmDialog(frame,
                                                          Resources.getString("Save.confirmOverwrite"),
                                                          Resources.getString("Save.name"),
                                                          JOptionPane.YES_NO_OPTION,
                                                          JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
                            continue;
                        }
                    }

                    preferredDirectoryManager.setPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS, directory);

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
