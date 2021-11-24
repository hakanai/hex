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

package org.trypticon.hex.gui.scripting;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.BaseAction;
import org.trypticon.hex.gui.util.platform.Platform;

/**
 * Action to open a file browser at the script directory.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class OpenScriptDirectoryAction extends BaseAction {
    public OpenScriptDirectoryAction() {
        Resources.localiseAction(this, "OpenScriptDirectory");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        File dir = Platform.getCurrent().getScriptsDirs().get(0);
        if (!dir.exists()) {
            Files.createDirectories(dir.toPath());
            //TODO: Could possibly drop a symlink in the directory linking go the docs.
        }
        Desktop.getDesktop().open(dir);
    }
}
