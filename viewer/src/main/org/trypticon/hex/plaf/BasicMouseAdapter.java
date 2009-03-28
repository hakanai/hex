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
        HexViewer viewer = (HexViewer) event.getSource();
        viewer.requestFocusInWindow();

        if (javax.swing.SwingUtilities.isLeftMouseButton(event)) {
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
