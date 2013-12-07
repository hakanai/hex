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

package org.trypticon.hex.gui.prefs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;

import org.trypticon.hex.gui.HexFrame;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.util.LoggerUtils;

/**
 * Support for loading and saving the state of the workspace.
 *
 * @author trejkaz
 */
public class WorkspaceStateTracker {

    /**
     * Gets the preferences node to use for storing the state.
     *
     * @return the preferences node.
     */
    private Preferences getPrefs() {
        return Preferences.userRoot().node("org/trypticon/hex/gui/prefs/workspace");
    }

    /**
     * Saves the workspace state to preferences.
     */
    public void save() {
        HexFrame frame = HexFrame.findActiveFrame();
        if (frame == null) {
            getPrefs().node("openDocuments").putInt("count", 0);
            return;
        }

        Preferences framePositionPrefs = getPrefs().node("framePosition");
        framePositionPrefs.putInt("x", frame.getX());
        framePositionPrefs.putInt("y", frame.getY());
        framePositionPrefs.putInt("width", frame.getWidth());
        framePositionPrefs.putInt("height", frame.getHeight());

        Preferences openDocumentPrefs = getPrefs().node("openDocuments");
        int count = 0;
        for (NotebookPane pane : frame.getAllNotebookPanes()) {
            if (pane.getNotebook().getNotebookLocation() == null) {
                // User must have explicitly chosen *not* to save the notebook, so toss it.
                continue;
            }

            openDocumentPrefs.put("location" + count, pane.getNotebook().getNotebookLocation().toExternalForm());
            count++;
        }

        openDocumentPrefs.putInt("count", count);
    }

    /**
     * Restores the workspace to the saved state.
     *
     * @return {@code true} if the state was restored, {@code false} if there was no state stored.
     */
    public boolean restore() {
        Preferences openDocumentPrefs = getPrefs().node("openDocuments");
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
                HexFrame.openNotebook(notebook);
            } catch (MalformedURLException e) {
                LoggerUtils.get().log(Level.WARNING, "Malformed URL found in preferences for document " + i + ": " +
                                                     location + ", skipping", e);
            } catch (IOException e) {
                LoggerUtils.get().log(Level.WARNING, "Error opening previously-open notebook: " + location + ", skipping");
            }
        }

        // Only restore preferences if a frame is open.  A frame is opened by default before this occurs,
        // for most platforms.  The exception is Mac, where if there were no documents open last session,
        // the frame will not be open.

        // Have to give the window system a chance to put the window up, if it just appeared.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (HexFrame.findActiveFrame() != null) {
                    restoreFrameLocation(HexFrame.findActiveFrame());
                }
            }
        });

        return true;
    }

    /**
     * Restores the location of the given frame.
     *
     * @param frame the frame.
     */
    public void restoreFrameLocation(final HexFrame frame) {
        Preferences framePositionPrefs = getPrefs().node("framePosition");
        final int x = framePositionPrefs.getInt("x", -1);
        final int y = framePositionPrefs.getInt("y", -1);
        final int width = framePositionPrefs.getInt("width", -1);
        final int height = framePositionPrefs.getInt("height", -1);

        if (x >= 0 && y >= 0 && width >= 0 && height >= 0) {
            frame.setBounds(x, y, width, height);
        }
    }
}
