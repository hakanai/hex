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

package org.trypticon.hex.gui.datatransfer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.FinalizeGuardian;
import org.trypticon.hex.gui.util.FocusedComponentAction;

/**
 * Action to paste from the clipboard.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class PasteAction extends FocusedComponentAction {
    private final FlavorListener listener = (event) -> {
        updateEnabled();
    };

    @SuppressWarnings({"UnusedDeclaration", "UnusedVariable"})
    private final Object finalizeGuardian = new FinalizeGuardian(() -> {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.removeFlavorListener(listener);
    });

    public PasteAction() {
        Resources.localiseAction(this, "Paste");

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.addFlavorListener(listener);
    }

    @Override
    protected boolean shouldBeEnabled(@Nonnull JComponent focusOwner) {
        TransferHandler transferHandler = focusOwner.getTransferHandler();
        if (transferHandler == null) {
            return false;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        DataFlavor[] flavorsInClipboard = clipboard.getAvailableDataFlavors();
        return transferHandler.canImport(focusOwner, flavorsInClipboard);
    }

    @Override
    protected void doAction(@Nonnull JComponent focusOwner) throws Exception {
        Action action = TransferHandler.getPasteAction();
        action.actionPerformed(new ActionEvent(
            focusOwner, ActionEvent.ACTION_PERFORMED, (String) action.getValue(Action.NAME)));
    }
}
