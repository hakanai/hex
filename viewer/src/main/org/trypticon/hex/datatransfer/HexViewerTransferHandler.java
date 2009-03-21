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

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.HexUtils;
import org.trypticon.hex.HexViewer;

/**
 * Transfer handler for clipboard-related methods.
 *
 * @author trejkaz
 */
public class HexViewerTransferHandler extends TransferHandler {

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        HexViewer viewer = (HexViewer) c;

        Binary binary = viewer.getBinary();
        long selectionStart = viewer.getSelectionModel().getSelectionStart();
        long selectionEnd = viewer.getSelectionModel().getSelectionEnd();

        return new StringSelection(HexUtils.toHex(binary, selectionStart, selectionEnd));
    }
}
