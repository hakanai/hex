/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.trypticon.hex.gui.util;

import java.io.File;import java.lang.Override;import java.lang.String;
import javax.swing.JFileChooser;

/**
 * Workarounds for limitations in the real {@link JFileChooser}.
 *
 * @author trejkaz
 */
public class ImprovedFileChooser extends JFileChooser {
    @Override
    public File getSelectedFile() {
        File file = super.getSelectedFile();
        if (file == null || !(getFileFilter() instanceof FileExtensionFilter)) {
            return file;
        }

        FileExtensionFilter filter = (FileExtensionFilter) getFileFilter();

        String name = file.getName();
        if (!name.endsWith(filter.getExtension())) {
            file = new File(file.getParentFile(), name + '.' + filter.getExtension());
        }

        return file;
    }
}
