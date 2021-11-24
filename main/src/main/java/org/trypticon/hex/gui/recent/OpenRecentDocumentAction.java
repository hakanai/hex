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

package org.trypticon.hex.gui.recent;

import java.awt.event.ActionEvent;
import java.nio.file.Path;

import org.trypticon.hex.gui.HexApplication;
import org.trypticon.hex.gui.util.BaseAction;

/**
 * Action to open a recent document.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
class OpenRecentDocumentAction extends BaseAction {
    private final HexApplication application;
    private final Path path;

    OpenRecentDocumentAction(HexApplication application, Path path) {
        this.application = application;
        this.path = path;

        putValue(NAME, path.getFileName().toString());
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        application.openNotebook(path);
    }
}
