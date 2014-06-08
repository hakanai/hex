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

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.BaseAction;

/**
 * Action to clear the Open Recent menu.
 *
 * @author trejkaz
 */
public class ClearOpenRecentMenuAction extends BaseAction {
    private final RecentDocumentsModel model;

    public ClearOpenRecentMenuAction(RecentDocumentsModel model) {
        this.model = model;
        updateEnabled();

        Resources.localiseAction(this, "ClearOpenRecentMenu");

        model.addChangeListener(event -> updateEnabled());
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        model.clear();
    }

    @Override
    protected boolean shouldBeEnabled() {
        return model != null && !model.isEmpty();
    }
}
