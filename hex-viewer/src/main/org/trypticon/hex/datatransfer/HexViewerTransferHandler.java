/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.datatransfer;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.trypticon.binary.Binary;
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
