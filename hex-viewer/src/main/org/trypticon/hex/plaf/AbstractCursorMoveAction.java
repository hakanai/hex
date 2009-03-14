/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.trypticon.hex.HexViewer;

/**
 * Base class for actions which move the cursor.
 *
 * @author trejkaz
 */
abstract class AbstractCursorMoveAction extends AbstractAction {
    public void actionPerformed(ActionEvent event) {
        HexViewer viewer = (HexViewer) event.getSource();

        long newCursorPos = viewer.getSelectionModel().getCursor() + getShift(viewer);
        long length = viewer.getBinary().length();

        if (newCursorPos >= length) {
            newCursorPos = length - 1;
            Toolkit.getDefaultToolkit().beep();
        } else if (newCursorPos < 0) {
            newCursorPos = 0;
            Toolkit.getDefaultToolkit().beep();
        }

        moveCursorTo(viewer, newCursorPos);

        viewer.scrollRectToVisible(viewer.getBoundsForPosition(newCursorPos));
    }

    /**
     * Called to move the cursor.
     *
     * @param viewer the hex viewer.
     * @param newCursorPos the new cursor position.
     */
    void moveCursorTo(HexViewer viewer, long newCursorPos) {
        viewer.getSelectionModel().setCursor(newCursorPos);
    }

    /**
     * Gets the amount by which the cursor should shift when performing this action (positive is forwards, negative is
     * backwards.)  The value returned can bring the cursor to an illegal position; this is properly checked before
     * actually setting the position.
     *
     * @param viewer the hex viewer.
     * @return the amount by which the cursor should shift.
     */
    protected abstract int getShift(HexViewer viewer);

}
