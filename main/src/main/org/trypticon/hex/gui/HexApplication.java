/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2012  Trejkaz, Hex Project
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
import java.awt.Window;
import java.io.IOException;
import javax.swing.DefaultFocusManager;
import javax.swing.JOptionPane;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Manages the top-level application logic.
 *
 * @author trejkaz
 */
public class HexApplication {
    private static final HexApplication instance = new HexApplication();

    /**
     * Gets the singleton instance of the application.
     *
     * @return the application.
     */
    public static HexApplication get() {
        return instance;
    }

    //TODO: Application startup logic should eventually move in here.

    //TODO: Exit logic should eventually move in here.

    /**
     * <p>Opens a notebook.</p>
     *
     * <p>May open a new frame or may use an existing frame. This depends on what platform you're running on.</p>
     *
     * @param notebook the notebook.
     * @return the frame the notebook was opened in, or {@code null} if there was a problem opening it
     *         (in this situation the user would have been alerted already.)
     */
    public HexFrame openNotebook(Notebook notebook) {
        Window activeWindow = DefaultFocusManager.getCurrentManager().getActiveWindow();

        try {
            notebook.open();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(activeWindow, "There was a problem opening the notebook: " + e.getMessage(),
                                          "Error Opening File", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (PLAFUtils.isAqua()) {
            // Try to mimic document-based Mac applications better by using a separate frame per notebook.
            SingleHexFrame frame = new SingleHexFrame(notebook);
            frame.setVisible(true);
            return frame;
        } else {
            MultipleHexFrame frame = (MultipleHexFrame) HexFrame.findActiveFrame();
            frame.addTab(notebook);
            return frame;
        }
    }
}
