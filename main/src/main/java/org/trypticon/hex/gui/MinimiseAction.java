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

package org.trypticon.hex.gui;

import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.Nullable;

import org.trypticon.hex.gui.util.BaseAction;

/**
 * Action to minimise the window.
 *
 * @author trejkaz
 */
public class MinimiseAction extends BaseAction {
    private final PropertyChangeListener activeWindowListener = event -> {
        currentActiveWindow = (Window) event.getNewValue();
        updateEnabled();
    };

    @Nullable
    private Window currentActiveWindow;

    public MinimiseAction() {
        Resources.localiseAction(this, "Minimise");

        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener("activeWindow", activeWindowListener);
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        if (window instanceof Frame) {
            Frame frame = (Frame) window;
            frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
        }
    }

    @Override
    protected boolean shouldBeEnabled() {
        return currentActiveWindow instanceof Frame &&
               (((Frame) currentActiveWindow).getExtendedState() & Frame.ICONIFIED) == 0;
    }
}

