/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.trypticon.hex.gui.prefs.WorkspaceStateTracker;
import org.trypticon.hex.gui.util.Callback;

/**
 * Action to exit the application.
 *
 * @author trejkaz
 */
class ExitAction extends AbstractAction {
    private final HexApplication application;

    ExitAction(HexApplication application) {
        this.application = application;
        putValue(NAME, "Exit");
        putValue(MNEMONIC_KEY, (int) 'x');
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        application.tryToExit(new Callback<Boolean>() {
            @Override
            public void execute(Boolean okToExit) {
                if (okToExit) {
                    System.exit(0);
                }
            }
        });
    }

}
