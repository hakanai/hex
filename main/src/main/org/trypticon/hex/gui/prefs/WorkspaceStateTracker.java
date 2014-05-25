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

import java.util.prefs.Preferences;

import org.trypticon.hex.gui.HexApplication;
import org.trypticon.hex.gui.HexFrame;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Support for loading and saving the state of the workspace.
 *
 * @author trejkaz
 */
public abstract class WorkspaceStateTracker {

    /**
     * Gets the preferences node to use for storing the state.
     *
     * @return the preferences node.
     */
    protected Preferences getPrefs() {
        return Preferences.userRoot().node("org/trypticon/hex/gui/prefs/workspace");
    }

    /**
     * Saves a frame location.
     *
     * @param frame the frame.
     * @param node the node to save the location to.
     */
    protected void saveFrameLocation(final HexFrame frame, final Preferences node) {
        node.putInt("x", frame.getX());
        node.putInt("y", frame.getY());
        node.putInt("width", frame.getWidth());
        node.putInt("height", frame.getHeight());
    }

    /**
     * Restores a frame location. This is done on the EDT at a later time so that the
     * window system gets a chance to make the window visible first.
     *
     * @param frame the frame.
     * @param node the node to restore the location from.
     */
    protected void restoreFrameLocation(final HexFrame frame, final Preferences node) {
        final int x = node.getInt("x", -1);
        final int y = node.getInt("y", -1);
        final int width = node.getInt("width", -1);
        final int height = node.getInt("height", -1);

        if (x >= 0 && y >= 0 && width >= 0 && height >= 0) {
            frame.setBounds(x, y, width, height);
        }
    }

    /**
     * Saves the workspace state to preferences.
     */
    public abstract void save();

    /**
     * Restores the workspace to the saved state.
     *
     * @return {@code true} if the state was restored, {@code false} if there was no state stored.
     */
    public abstract boolean restore();

    /**
     * Creates an appropriate instance of the workspace state tracker for the current platform.
     *
     * @param application the application.
     * @return the tracker.
     */
    public static WorkspaceStateTracker create(HexApplication application) {
        if (PLAFUtils.isAqua()) {
            return new AquaWorkspaceStateTracker(application);
        } else {
            return new DefaultWorkspaceStateTracker(application);
        }
    }
}
