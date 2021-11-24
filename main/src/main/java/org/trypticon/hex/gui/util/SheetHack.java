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
import javax.swing.JDialog;

import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Workarounds for "sheet" UIs.
 *
 * @author trejkaz
 */
public class SheetHack {
    /**
     * If running on Aqua look and feel, makes the given dialog a sheet.
     *
     * @param dialog the dialog.
     */
    public static void makeSheet(JDialog dialog) {
        //HACK: Set the window to display as a sheet.
        if (PLAFUtils.isAqua()) {
            dialog.removeNotify();
            dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
            dialog.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
            dialog.addNotify();
        }
    }
}
