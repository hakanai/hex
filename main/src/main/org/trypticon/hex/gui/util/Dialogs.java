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

package org.trypticon.hex.gui.util;

import java.awt.Dialog;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Dialog utilities.
 *
 * @author trejkaz
 */
public class Dialogs {

    /**
     * Pops up a heavyweight popup (a dialog) which will automatically close when focus is lost.
     *
     * @param parentComponent the component to pop up relative to.
     * @param dialogContent the content of the dialog.
     * @param locationFromParentComponent the location to pop up relative to {@code parentComponent}.
     * @return the created dialog.
     */
    public static JDialog popupAutoClosingModalDialog(JComponent parentComponent,
                                                      JComponent dialogContent,
                                                      Point locationFromParentComponent) {
        Window owner = SwingUtilities.getWindowAncestor(parentComponent);
        JDialog dialog = new JDialog(owner, Dialog.ModalityType.MODELESS);

        autoCloseOnLosingFocus(dialog);

        dialog.setContentPane(dialogContent);
        dialog.setUndecorated(true);
        dialog.pack();
        Point location = parentComponent.getLocationOnScreen();
        dialog.setLocation(location.x + locationFromParentComponent.x,
                           location.y + locationFromParentComponent.y);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return dialog;
    }

    /**
     * Auto-closes the given dialog when focus is lost.
     *
     * @param dialog the dialog.
     */
    private static void autoCloseOnLosingFocus(JDialog dialog) {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        PropertyChangeListener focusedWindowListener = event -> {
            Window focusedWindow = (Window) event.getNewValue();

            Window owner = dialog.getOwner();
            while (owner != null) {
                if (owner == focusedWindow) {
                    dialog.dispose();
                    break;
                } else {
                    owner = owner.getOwner();
                }
            }
        };
        focusManager.addPropertyChangeListener("focusedWindow", focusedWindowListener);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                dialog.removeWindowListener(this);
                focusManager.removePropertyChangeListener("focusedWindow", focusedWindowListener);
            }
        });
    }
}
