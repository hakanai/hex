/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

import org.trypticon.hex.HexViewer;

/**
 * Handles mouse events on the viewer.
 *
 * @author trejkaz
 */
class BasicMouseAdapter extends MouseInputAdapter {

    @Override
    public void mousePressed(MouseEvent event) {
        if (javax.swing.SwingUtilities.isLeftMouseButton(event)) {
            HexViewer viewer = (HexViewer) event.getSource();

            // TODO: If it's within the address lines it might be better to select
            //       the row and then have dragging select further rows.

            long pos = viewer.getPositionForPoint(event.getPoint());
            viewer.getSelectionModel().setCursor(pos);
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (javax.swing.SwingUtilities.isLeftMouseButton(event)) {
            HexViewer viewer = (HexViewer) event.getSource();

            long pos = viewer.getPositionForPoint(event.getPoint());
            viewer.getSelectionModel().setCursorAndExtendSelection(pos);

            // TODO: An option for disabling autoscroll on selection would
            //       fit with the rest of Swing but I don't need it immediately.

            viewer.scrollRectToVisible(viewer.getBoundsForPosition(pos));
        }
    }
}
