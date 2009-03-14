/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import org.trypticon.hex.HexViewer;

/**
 * Action to move the selection up one row.
 *
 * @author trejkaz
 */
class SelectionUpAction extends AbstractSelectionMoveAction {
    @Override
    protected int getShift(HexViewer viewer) {
        return -viewer.getBytesPerRow();
    }
}
