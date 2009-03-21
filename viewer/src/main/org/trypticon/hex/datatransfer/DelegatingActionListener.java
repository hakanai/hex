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

package org.trypticon.hex.datatransfer;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

/**
 * <p>Tracks the current component, and forwards actions received onto that component.</p> <p/> <p>The easiest way to
 * use this is via {@link JMenuItem#setActionCommand(String)}. The same action listener can then be reused for multiple
 * menu items.</p>
 *
 * @author trejkaz
 */
public class DelegatingActionListener implements ActionListener, PropertyChangeListener {
    private JComponent focusOwner = null;

    // Guards against this listener on a global object causing memory leaks.
    @SuppressWarnings({"UnusedDeclaration"})
    private final Object finalizeGuardian = new Object() {
        @Override
        protected void finalize() {
            KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            manager.removePropertyChangeListener("permanentFocusOwner", DelegatingActionListener.this);
        }
    };

    public DelegatingActionListener() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
    }

    public void propertyChange(PropertyChangeEvent event) {
        Object o = event.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent) o;
        } else {
            focusOwner = null;
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (focusOwner == null) {
            return;
        }

        String command = event.getActionCommand();
        Action action = focusOwner.getActionMap().get(command);
        if (action != null) {
            action.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
        }
    }
}
