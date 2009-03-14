/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import org.trypticon.hex.HexViewer;

/**
 * Action to move the selection right one column.
 *
 * @author trejkaz
 */
class SelectionRightAction extends AbstractSelectionMoveAction {
    @Override
    protected int getShift(HexViewer viewer) {
        return 1;
    }
}
