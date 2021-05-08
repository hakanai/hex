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

import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.swing.Action;
import javax.swing.JComponent;

import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.gui.Resources;

/**
 * <p>Tracks the current component, and forwards actions received onto that component.</p>
 *
 * @author trejkaz
 */
public class DelegatingAction extends FocusedComponentAction {
    private final String delegateAction;

    public DelegatingAction(@NonNls String baseKey, @NonNls String delegateAction) {
        this.delegateAction = delegateAction;
        Resources.localiseAction(this, baseKey);
    }

    @Override
    protected boolean shouldBeEnabled(@Nonnull JComponent focusOwner) {
        Action action = focusOwner.getActionMap().get(delegateAction);
        return action != null && action.isEnabled();
    }

    @Override
    protected void doAction(@Nonnull JComponent focusOwner) throws Exception {
        Action action = focusOwner.getActionMap().get(delegateAction);
        if (action != null) {
            action.actionPerformed(new ActionEvent(focusOwner, ActionEvent.ACTION_PERFORMED, null));
        }
    }

}
