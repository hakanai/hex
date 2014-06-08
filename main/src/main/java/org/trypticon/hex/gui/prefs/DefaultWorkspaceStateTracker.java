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

package org.trypticon.hex.gui.prefs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import org.trypticon.hex.gui.HexApplication;
import org.trypticon.hex.gui.HexFrame;
import org.trypticon.hex.gui.MultipleHexFrame;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.util.LoggerUtils;

/**
 * Workspace state tracker used for non-Mac platforms.
 *
 * @author trejkaz
 */
class DefaultWorkspaceStateTracker extends WorkspaceStateTracker {
    private final HexApplication application;

    DefaultWorkspaceStateTracker(HexApplication application) {
        this.application = application;
    }

    @Override
    public void save() {
        MultipleHexFrame frame = (MultipleHexFrame) HexFrame.findActiveFrame();
        Preferences framePrefs = getPrefs();
        if (frame == null) {
            framePrefs.node("openDocuments").putInt("count", 0);
            return;
        }

        saveFrameLocation(frame, framePrefs);

        Preferences openDocumentPrefs = framePrefs.node("openDocuments");
        int count = 0;
        for (NotebookPane pane : frame.getAllNotebookPanes()) {
            URL notebookLocation = pane.getNotebook().getNotebookLocation();
            if (notebookLocation == null) {
                // User must have explicitly chosen *not* to save the notebook, so toss it.
                continue;
            }

            openDocumentPrefs.put("location" + count, notebookLocation.toExternalForm());
            count++;
        }

        openDocumentPrefs.putInt("count", count);
    }

    @Override
    public boolean restore() {
        final MultipleHexFrame frame = (MultipleHexFrame) HexFrame.findActiveFrame();
        final Preferences framePrefs = getPrefs();
        Preferences openDocumentPrefs = framePrefs.node("openDocuments");
        int count = openDocumentPrefs.getInt("count", -1);
        if (count < 0) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            String location = openDocumentPrefs.get("location" + i, null);
            if (location == null) {
                LoggerUtils.get().warning("Location for open document " + i + " missing, skipping");
                continue;
            }

            try {
                URL url = new URL(location);
                Notebook notebook = new NotebookStorage().read(url);
                application.openNotebook(notebook);
            } catch (MalformedURLException e) {
                LoggerUtils.get().log(Level.WARNING, "Malformed URL found in preferences for document " + i + ": " +
                                                     location + ", skipping", e);
            } catch (IOException e) {
                LoggerUtils.get().log(Level.WARNING, "Error opening previously-open notebook: " + location + ", skipping");
            }
        }

        restoreFrameLocation(frame, framePrefs.node("frameLocation"));

        return true;
    }
}
