/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import org.trypticon.hex.HexViewer;

/**
 * Base class for actions which move the cursor and extend the selection.
 *
 * @author trejkaz
 */
abstract class AbstractSelectionMoveAction extends AbstractCursorMoveAction {
    @Override
    void moveCursorTo(HexViewer viewer, long newCursorPos) {
        viewer.getSelectionModel().setCursorAndExtendSelection(newCursorPos);
    }
}
