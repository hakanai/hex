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

package org.trypticon.hex.gui.file;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.trypticon.hex.gui.NotebookPaneAction;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.util.LoggerUtils;

/**
 * Action to revert to the saved copy.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class RevertToSavedAction extends NotebookPaneAction {
    public RevertToSavedAction() {
        Resources.localiseAction(this, "RevertToSaved");
    }

    @Override
    protected void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception {
        Path location = notebookPane.getNotebook().getNotebookLocation();
        if (location != null) {
            String name = location.getFileName().toString();
            new RevertToSavedConfirmation().show(notebookPane, name, result -> {
                if (result == RevertToSavedConfirmation.Option.REVERT) {
                    Notebook newNotebook;
                    try {
                        newNotebook = new NotebookStorage().read(location);
                    } catch (Exception e) {
                        LoggerUtils.get().log(Level.SEVERE, "Unexpected error reverting notebook", e);

                        JXErrorPane.showDialog(notebookPane, new ErrorInfo(
                            Resources.getString("UnexpectedError.title"),
                            Resources.getString("UnexpectedError.text"),
                            null, null, e, Level.SEVERE, null));

                        return;
                    }

                    try {
                        newNotebook.open();
                    } catch (IOException e) {
                        String message = Objects.requireNonNullElse(e.getLocalizedMessage(), "");
                        JOptionPane.showMessageDialog(notebookPane,
                                                      Resources.getString("Notebook.errorOpening", message),
                                                      Resources.getString("Notebook.errorOpeningTitle"),
                                                      JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Closes the old one since it has its own handle to the binary file.
                    notebookPane.getNotebook().close();

                    notebookPane.setNotebook(newNotebook);
                }
            });
        }
    }

    @Override
    protected boolean shouldBeEnabled(NotebookPane notebookPane) {
        return notebookPane.getNotebook().getNotebookLocation() != null;
    }
}
