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

package org.trypticon.hex.gui.file;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

import org.trypticon.hex.gui.HexApplication;
import org.trypticon.hex.gui.HexFrame;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.notebook.DefaultNotebook;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.gui.util.BaseAction;
import org.trypticon.hex.gui.util.FileSelection;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Action to open a new binary file, creating a new notebook for annotating it.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class NewNotebookAction extends BaseAction {
    private final HexApplication application;
    private final PreferredDirectoryManager preferredDirectoryManager;

    public NewNotebookAction(HexApplication application) {
        this.application = application;
        this.preferredDirectoryManager = application.getPreferredDirectoryManager();

        Resources.localiseAction(this, "New");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        // For Mac OS X, when opening files, the file chooser is *not* parented by the current window.
        Window activeWindow = PLAFUtils.isAqua() ? null : HexFrame.findActiveFrame();

        File file = FileSelection.getInstance().selectFile(
            activeWindow, FileSelection.Mode.LOAD,
            preferredDirectoryManager.getPreferredDirectory(PreferredDirectoryManager.BINARIES), null);

        if (file != null) {
            if (!file.isFile()) {
                //XXX: Probably impossible. Or at the very least, it should be the file chooser's job.
                throw new ActionException(Resources.getMessage("Common.Errors.notFile"));
            }

            preferredDirectoryManager.setPreferredDirectory(PreferredDirectoryManager.BINARIES, file.getParentFile());

            application.openNotebook(new DefaultNotebook(file.toPath()));
        }
    }
}
