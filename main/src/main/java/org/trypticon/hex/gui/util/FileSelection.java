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

package org.trypticon.hex.gui.util;

import java.awt.Component;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Abstraction of file selection methods.
 *
 * @author trejkaz
 */
public abstract class FileSelection {
    /**
     * Gets an instance of the save confirmation utility appropriate for the current Look and Feel.
     *
     * @return a new save confirmation support object.
     */
    public static FileSelection getInstance() {
        if (PLAFUtils.isAqua()) {
            return new AwtFileSelection();
        } else {
            return new SwingFileSelection();
        }
    }

    /**
     * Selects a file.
     *
     * @param parentComponent a parent component for the dialog.
     * @param mode indicates whether the file is being loaded or saved.
     * @param initialDirectory the directory to start at.
     * @param fileFilter a filter indicating the type of file the user is selecting.
     * @return the selected file. Returns {@code null} if the user cancels.
     */
    @Nullable
    public abstract File selectFile(@Nullable Component parentComponent,
                                    @Nonnull Mode mode,
                                    @Nonnull File initialDirectory,
                                    @Nullable FileExtensionFilter fileFilter);

    /**
     * Enumeration of modes file selection can operate in.
     */
    public enum Mode {
        /**
         * File will be loaded.
         */
        LOAD,

        /**
         * File will be saved.
         */
        SAVE
    }
}
