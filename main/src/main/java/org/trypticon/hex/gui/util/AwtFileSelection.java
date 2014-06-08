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
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * File selection using AWT's {@link FileDialog}.
 */
public class AwtFileSelection extends FileSelection {
    @Override
    public File selectFile(@Nullable Component parentComponent,
                           @NotNull Mode mode,
                           @NotNull File initialDirectory,
                           @NotNull FileExtensionFilter fileFilter) {
        Window owner = parentComponent == null ? null :
                       parentComponent instanceof Window ? (Window) parentComponent :
                       SwingUtilities.getWindowAncestor(parentComponent);
        FileDialog dialog = owner instanceof Frame ? new FileDialog((Frame) owner)
                                                   : new FileDialog((Dialog) owner);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        if (owner != null) {
//            SheetHack.makeSheet(dialog);
        }
        dialog.setMode(mode == Mode.LOAD ? FileDialog.LOAD : FileDialog.SAVE);
        dialog.setDirectory(initialDirectory.getAbsolutePath());
        dialog.setFilenameFilter(fileFilter);
        dialog.setVisible(true);
        File[] files = dialog.getFiles();
        return files.length == 0 ? null : files[0];
    }
}
