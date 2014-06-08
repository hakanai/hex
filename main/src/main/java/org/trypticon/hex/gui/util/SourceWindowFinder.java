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

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;

import org.trypticon.hex.util.LoggerUtils;

/**
 * Utility to find the source window for an event.
 *
 * @author trejkaz
 */
public class SourceWindowFinder {

    /**
     * Finds the source window for an event.
     *
     * @param event the event.
     * @return the source window.
     */
    public Window findSourceWindow(ActionEvent event) {
        Object eventSource = event.getSource();
        if (eventSource instanceof Component) {
            Window source = getRootWindow((Component) eventSource);
            if (source != null) {
                return source;
            }
        }

        // Occurs on Mac OS when running actions from the main menu, because the main menu's parent window
        // is some kind of special hidden window.
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
    }

    /**
     * Gets the root window for the given component.
     *
     * @param component the component.
     * @return the root window for that component.  Returns {@code null} if the root is not a window, but
     *         this would suggest the need for a fix to the code.
     */
    private Window getRootWindow(Component component) {
        Component root = getRoot(component);

        if (root instanceof Window) {
            return (Window) root;
        } else {
            LoggerUtils.get().warning("Top-level component was not a window: " + root);
            return null;
        }

    }

    /**
     * Gets the root of the given component.  Different from the method in {@code SwingUtilities}
     * because Swing doesn't parent popup menus.
     *
     * @param component the component.
     * @return the root component.  This never returns {@code null}.
     */
    private Component getRoot(Component component) {
        Component root = component;
        Component parent;
        while ((parent = getParent(root)) != null) {
            root = parent;
        }
        return root;
    }

    /**
     * Gets the parent of the given component.  Different from just getting the component's parent
     * through the Swing API because Swing doesn't parent popup menus.
     *
     * @param component the component.
     * @return the parent component.  Returns null if the component is at the top of the hierarchy.
     */
    private Component getParent(Component component) {
        if (component instanceof JPopupMenu) {
            return ((JPopupMenu) component).getInvoker();
        } else {
            return component.getParent();
        }
    }

}
