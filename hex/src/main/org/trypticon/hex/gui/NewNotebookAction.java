/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.io.File;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.trypticon.hex.swingsupport.BaseAction;
import org.trypticon.hex.swingsupport.ActionException;
import org.trypticon.hex.gui.notebook.Notebook;

/**
 * Action to open a new binary file, creating a new notebook for annotating it.
 *
 * @author trejkaz
 */
class NewNotebookAction extends BaseAction {
    NewNotebookAction() {
        putValue(NAME, "New...");
        putValue(MNEMONIC_KEY, (int) 'n');
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    }

    protected void doAction(ActionEvent event) throws Exception {
        Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        HexFrame frame = (HexFrame) SwingUtilities.getWindowAncestor(owner);

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.isFile()) {
                throw new ActionException("Not a file: " + file);
            }

            frame.setNotebook(new Notebook(file.toURI().toURL()));
        }
    }
}
