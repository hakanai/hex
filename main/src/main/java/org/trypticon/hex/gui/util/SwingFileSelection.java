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
import javax.swing.JFileChooser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * File selection using Swing's {@link JFileChooser}.
 *
 * @author trejkaz
 */
public class SwingFileSelection extends FileSelection {
    @Override
    @Nullable
    public File selectFile(@Nullable Component parentComponent,
                           @NotNull Mode mode,
                           @NotNull File initialDirectory,
                           @Nullable FileExtensionFilter fileFilter) {
        JFileChooser chooser = new ImprovedFileChooser();
        if (fileFilter != null) {
            chooser.setFileFilter(fileFilter);
        }
        chooser.setCurrentDirectory(initialDirectory);
        int result;
        if (mode == Mode.LOAD) {
            result = chooser.showOpenDialog(parentComponent);
        } else {
            result = chooser.showSaveDialog(parentComponent);
        }
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return chooser.getSelectedFile();
    }
}
