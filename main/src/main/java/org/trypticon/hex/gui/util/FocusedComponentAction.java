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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;

/**
 * An action applied to the focused component.
 *
 * @author trejkaz
 */
public abstract class FocusedComponentAction extends BaseAction {

    @Nullable
    private JComponent focusOwner = null;

    private final PropertyChangeListener listener = (event) -> {
        Object o = event.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent) o;
        } else {
            focusOwner = null;
        }
        updateEnabled();
    };

    @SuppressWarnings({"UnusedDeclaration"})
    private final Object finalizeGuardian = new FinalizeGuardian(() -> {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.removePropertyChangeListener("permanentFocusOwner", listener);
    });

    protected FocusedComponentAction() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", listener);
    }

    @Override
    protected final boolean shouldBeEnabled() {
        return focusOwner != null && shouldBeEnabled(focusOwner);
    }

    /**
     * Called to test if the action should be enabled.
     *
     * @param focusOwner the currently focused component.
     * @return {@code true} if the action should be enabled, {@code false} otherwise.
     */
    protected abstract boolean shouldBeEnabled(@Nonnull JComponent focusOwner);

    /**
     * Called to perform the action.
     *
     * @param focusOwner the currently focused component.
     * @throws Exception if an error occurs performing the action.
     */
    protected abstract void doAction(@Nonnull JComponent focusOwner) throws Exception;

    @Override
    protected final void doAction(ActionEvent event) throws Exception {
        if (focusOwner != null) {
            //TODO: It might not be in focus.
            doAction(focusOwner);
        }
    }
}
