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

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.filechooser.FileSystemView;

import org.jetbrains.annotations.NonNls;

/**
 * Support for tracking preferred directories.
 *
 * @author trejkaz
 */
public class PreferredDirectoryManager {
    public static final Key BINARIES = new Key("binaries");
    public static final Key NOTEBOOKS = new Key("notebooks");

    /**
     * Gets the preferences node to use for storing the state.
     *
     * @return the preferences node.
     */
    private Preferences getPrefs() {
        return Preferences.userRoot().node("org/trypticon/hex/gui/prefs/directories");
    }

    /**
     * Gets the preferred directory for a given key.  If there is no preference the system will attempt
     * to return a sensible default.
     *
     * @param key the key to look up.
     * @return the preferred directory.  Never returns {@code null}.
     */
    public File getPreferredDirectory(Key key) {
        String preferredPath = getPrefs().get(key.prefKey, null);
        if (preferredPath != null) {
            File preferredDirectory = new File(preferredPath);
            if (preferredDirectory.isDirectory()) {
                return preferredDirectory;
            }
        }

        return FileSystemView.getFileSystemView().getDefaultDirectory();
    }

    /**
     * Sets the preferred directory for a given key.
     *
     * @param key the key to set.
     * @param directory the preferred directory.
     */
    public void setPreferredDirectory(Key key, File directory) {
        getPrefs().put(key.prefKey, directory.getAbsolutePath());
    }

    /**
     * Key for looking up preferred directories.
     */
    public static class Key {
        private final String prefKey;

        private Key(@NonNls String prefKey) {
            this.prefKey = prefKey;
        }
    }
}
