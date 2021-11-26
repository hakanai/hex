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
import org.trypticon.hex.gui.notebook.NotebookFileFilter;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.recent.RecentDocumentsModel;
import org.trypticon.hex.gui.util.ActionException;
import org.trypticon.hex.gui.util.BaseAction;
import org.trypticon.hex.gui.util.FileSelection;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Action to open an existing notebook.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class OpenNotebookAction extends BaseAction {
    private final HexApplication application;
    private final PreferredDirectoryManager preferredDirectoryManager;
    private final RecentDocumentsModel recentDocumentsModel;

    public OpenNotebookAction(HexApplication application) {
        this.application = application;
        this.preferredDirectoryManager = application.getPreferredDirectoryManager();
        this.recentDocumentsModel = application.getRecentDocumentsModel();

        Resources.localiseAction(this, "Open");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        // For Mac OS X, when opening files, the file chooser is *not* parented by the current window.
        Window activeWindow = PLAFUtils.isAqua() ? null : HexFrame.findActiveFrame();

        FileSelection fileSelection = FileSelection.getInstance();

        File file = fileSelection.selectFile(
                activeWindow, FileSelection.Mode.LOAD,
                preferredDirectoryManager.getPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS),
                new NotebookFileFilter());
        if (file != null) {
            // XXX: This check may no longer be needed.
            if (!file.isFile()) {
                throw new ActionException(Resources.getMessage("Common.Errors.notFile"));
            }

            preferredDirectoryManager.setPreferredDirectory(PreferredDirectoryManager.NOTEBOOKS, file.getParentFile());
            recentDocumentsModel.addRecentDocument(file.toPath());

            application.openNotebook(file.toPath());
        }
    }
}
