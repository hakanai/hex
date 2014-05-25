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
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.trypticon.hex.gui.Resources;

/**
 * Default save confirmation support, using the option pane's built-in dialog support.
 *
 * @author trejkaz
 */
public class DefaultSaveConfirmation extends SaveConfirmation {
    @Override
    protected void showOptionPane(Component owner, JOptionPane optionPane, Callback<Object> callback) {
        JDialog dialog = optionPane.createDialog(owner, Resources.getString("Notebook.saveConfirmationTitle"));
        dialog.setVisible(true);
        // setVisible is synchronous so the value will have already been set by this point.
        callback.execute(optionPane.getValue());
    }
}
