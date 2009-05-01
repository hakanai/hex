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

package org.trypticon.hex.util.swingsupport;

import java.awt.Component;

import javax.swing.JOptionPane;

import ch.randelshofer.quaqua.JSheet;
import ch.randelshofer.quaqua.SheetEvent;
import ch.randelshofer.quaqua.SheetListener;

/**
 * Save confirmation implementation for Mac, where using a sheet is more friendly.
 */
public class QuaquaSaveConfirmation extends SaveConfirmation {
    protected Object showOptionPane(Component owner, JOptionPane optionPane) {
        class SaveSheetListener implements SheetListener {
            private Object selected;

            public void optionSelected(SheetEvent event) {
                selected = event.getValue();
            }
        }

        SaveSheetListener listener = new SaveSheetListener();

        JSheet.showSheet(optionPane, owner, listener);

        return listener.selected;
    }
}
