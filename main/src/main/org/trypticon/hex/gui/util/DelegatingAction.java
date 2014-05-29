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

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.JComponent;

import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.gui.Resources;

/**
 * <p>Tracks the current component, and forwards actions received onto that component.</p> <p/> <p>The easiest way to
 * use this is via {@link javax.swing.JMenuItem#setActionCommand(String)}. The same action listener can then be reused for multiple
 * menu items.</p>
 *
 * @author trejkaz
 */
public class DelegatingAction extends BaseAction {
    private final String delegateAction;
    private JComponent focusOwner = null;

    private final PropertyChangeListener listener = (event) -> {
        Object o = event.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent) o;
        } else {
            focusOwner = null;
        }
    };

    // Guards against this listener on a global object causing memory leaks.
    @SuppressWarnings({"UnusedDeclaration"})
    private final Object finalizeGuardian = new Object() {
        @Override
        protected void finalize() throws Throwable {
            try {
                KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                manager.removePropertyChangeListener("permanentFocusOwner", listener);
            } finally {
                super.finalize();
            }
        }
    };

    public DelegatingAction(@NonNls String baseKey, @NonNls String delegateAction) {
        this.delegateAction = delegateAction;
        Resources.localiseAction(this, baseKey);
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        if (focusOwner == null) {
            return;
        }

        //TODO: It might not be in focus.
        Action action = focusOwner.getActionMap().get(delegateAction);
        if (action != null) {
            action.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
        }
    }

}
