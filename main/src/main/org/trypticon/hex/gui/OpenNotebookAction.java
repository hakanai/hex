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

package org.trypticon.hex.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;

import org.trypticon.hex.gui.notebook.NotebookFileFilter;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.gui.util.BaseAction;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Action to open an existing notebook.
 *
 * @author trejkaz
 */
class OpenNotebookAction extends BaseAction {
    private final HexApplication application;
    private final PreferredDirectoryManager preferredDirectoryManager;

    public OpenNotebookAction(HexApplication application, PreferredDirectoryManager preferredDirectoryManager) {
        this.application = application;
        this.preferredDirectoryManager = preferredDirectoryManager;

        Resources.localiseAction(this, "Open");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        // For Mac OS X, when opening files, the file chooser is *not* parented by the current window.
        Window activeWindow = PLAFUtils.isAqua() ? null : HexFrame.findActiveFrame();

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new NotebookFileFilter());

        chooser.setCurrentDirectory(preferredDirectoryManager.getPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS));

        if (chooser.showOpenDialog(activeWindow) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.isFile()) {
                throw new ActionException(Resources.getMessage("Common.Errors.notFile"));
            }

            preferredDirectoryManager.setPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS, chooser.getCurrentDirectory());

            application.openNotebook(new NotebookStorage().read(file.toURI().toURL()));
        }
    }
}
