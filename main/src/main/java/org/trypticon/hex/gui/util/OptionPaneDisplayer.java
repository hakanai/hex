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
import javax.swing.JOptionPane;

/**
 * Abstracts methods for displaying an option pane.
 *
 * @author trejkaz
 */
public abstract class OptionPaneDisplayer {
    public static OptionPaneDisplayer getInstance() {
        //TODO: More native implementation for Mac OS X
        return new DefaultOptionPaneDisplay();
    }

    /**
     * Shows the option pane.  This may be done in different ways as appropriate for the
     * current Look and Feel.
     *
     * @param owner a component relative to which this will be displayed.  Generally a root pane.
     * @param dialogTitle the title to give the dialog.
     * @param optionPane the option pane to display.
     * @param callback a callback which receives the option selected by the user.  May be {@code null},
     *        in which case it is treated as if the user selected to cancel.
     */
    public abstract void showOptionPane(Component owner, String dialogTitle,
                                        JOptionPane optionPane, Callback<Object> callback);

}
