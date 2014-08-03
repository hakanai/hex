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

package org.trypticon.hex.gui.util.platform;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NonNls;

/**
 * Platform-specific utilities.
 *
 * @author trejkaz
 */
public abstract class Platform {
    private static final Platform current;
    static {
        @NonNls
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("mac os")) {
            current = new MacOSXPlatform();
        } else if (os.contains("windows")) {
            current = new WindowsPlatform();
        } else {
            current = new UnknownPlatform();
        }
    }

    public static Platform getCurrent() {
        return current;
    }

    public abstract List<File> getScriptsDirs();
}
