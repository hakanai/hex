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

package org.trypticon.hex.renderer;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.HexUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Default cell renderer implementation, using a Swing label as the component.
 */
public class DefaultCellRenderer extends JLabel implements CellRenderer {
    public DefaultCellRenderer() {
        setOpaque(true);
    }

    public Component getRendererComponent(HexViewer viewer, boolean selected, boolean onCursorRow, boolean atCursor,
                                          long position, int valueDisplayMode) {

        // XXX: I should probably split this logic into different renderers for each column.
        //int charYOffset = (rowHeight - metrics.getAscent()) / 2;

        setFont(viewer.getFont());

        setHorizontalAlignment(valueDisplayMode == ROW_OFFSET ? RIGHT : CENTER);

        Color background = viewer.getBackground();
        Color foreground;

        if (valueDisplayMode == ROW_OFFSET) {
            foreground = viewer.getOffsetForeground();
        } else {
            foreground = viewer.getForeground();

            if (onCursorRow && viewer.getCursorRowBackground() != null) {
                background = viewer.getCursorRowBackground();
            }

            if (selected && viewer.getSelectionBackground() != null) {
                background = viewer.getSelectionBackground();
            }
            if (selected && viewer.getSelectionForeground() != null) {
                foreground = viewer.getSelectionForeground();
            }

            if (atCursor && viewer.getCursorBackground() != null) {
                background = viewer.getCursorBackground();
            }
            if (atCursor && viewer.getCursorForeground() != null) {
                foreground = viewer.getCursorForeground();
            }
        }

        setBackground(background);
        setForeground(foreground);

        // XXX: This is redundant if rendering the address column.
        byte b = viewer.getBinary().read(position);
        String str;

        switch (valueDisplayMode) {
            case ROW_OFFSET:
                str = String.format("%08x", position);
                break;
            case HEX:
                str = HexUtils.toHex(b);
                break;
            case ASCII:
                str = HexUtils.toAscii(b);
                break;
            default:
                throw new IllegalStateException("Unimplemented display mode: " + valueDisplayMode);
        }

        setText(str);

        return this;
    }
}
