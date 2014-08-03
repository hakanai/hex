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

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.BaseAction;
import org.trypticon.hex.gui.util.platform.Platform;

/**
 * Action to open a file browser at the script directory.
 *
 * @author trejkaz
 */
public class OpenScriptDirectoryAction extends BaseAction {
    public OpenScriptDirectoryAction() {
        Resources.localiseAction(this, "OpenScriptDirectory");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        Desktop.getDesktop().open(Platform.getCurrent().getScriptsDirs().get(0));
    }
}
