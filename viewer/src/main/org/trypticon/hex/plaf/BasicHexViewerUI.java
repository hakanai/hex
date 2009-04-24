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

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.renderer.CellRenderer;

/**
 * Basic user interface for the hex viewer.
 *
 * @author trejkaz
 */
public class BasicHexViewerUI extends HexViewerUI {

    @Override
    public Rectangle modelToView(HexViewer viewer, long pos) {
        int bytesPerRow = viewer.getBytesPerRow();
        int charWidth = viewer.getFontMetrics(viewer.getFont()).charWidth('D');
        int rowHeight = viewer.getRowHeight();

        int bytesY = (int) (pos / bytesPerRow);
        int bytesX = (int) (pos - bytesPerRow * bytesY);

        int xFixed = bytesX * (3 * charWidth);
        int yFixed = bytesY * rowHeight;

        // Now adjust for the margins again...
        return new Rectangle(xFixed + 13 * charWidth,
                             yFixed + rowHeight,
                             3 * charWidth,
                             rowHeight);
    }

    @Override
    public long viewToModel(HexViewer viewer, Point point) {
        int charWidth = viewer.getFontMetrics(viewer.getFont()).charWidth('D');
        int rowHeight = viewer.getRowHeight();

        long binaryLength = viewer.getBinary().length();
        int bytesPerRow = viewer.getBytesPerRow();
        int maxBytesX = viewer.getBytesPerRow() - 1;
        long maxBytesY = (int) (binaryLength / viewer.getBytesPerRow());

        // Threshold for detecting that the user clicked in the ASCII column is half way between the two columns.
        int hexLeftX = 13 * charWidth;
        int hexRightX = hexLeftX + 3 * charWidth * bytesPerRow;
        int asciiLeftX = hexRightX + 2 * charWidth;

        int leftX;
        int cellWidth;

        if (point.x < (hexRightX + asciiLeftX) / 2) {
            // Clicked on the hex side.
            leftX = hexLeftX;
            cellWidth = 3 * charWidth;
        } else {
            // Clicked on the ASCII side.
            leftX = asciiLeftX;
            cellWidth = charWidth;
        }

        int xFixed = point.x - leftX;
        int yFixed = point.y - rowHeight;

        int bytesX = xFixed / cellWidth;
        int bytesY = yFixed / rowHeight;

        bytesX = Math.max(0, Math.min(maxBytesX, bytesX));
        bytesY = Math.max(0, (int) Math.min(maxBytesY, bytesY));

        long pos = (long) bytesY * bytesPerRow + bytesX;
        assert pos >= 0;
        if (pos >= binaryLength) {
            pos = binaryLength - 1;
        }

        return pos;
    }


    /**
     * Paints the component.  If the component is opaque, paints the background colour.  The rest of the painting is
     * delegated to {@link #paint(java.awt.Graphics2D, org.trypticon.hex.HexViewer).
     *
     * @param g the graphics context.
     * @param c the component.
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            Rectangle clip = g.getClipBounds();
            g.fillRect(0, 0, clip.width, clip.height);
        }

        paint((Graphics2D) g, (HexViewer) c);
    }

    /**
     * Paints the viewer.
     *
     * @param g the graphics context.
     * @param viewer the viewer.
     */
    protected void paint(Graphics2D g, HexViewer viewer) {
        Binary binary = viewer.getBinary();
        if (binary == null) {
            return;
        }

        g.setFont(viewer.getFont());
        FontMetrics metrics = g.getFontMetrics();
        Rectangle clipBounds = g.getClipBounds();

        int bytesPerRow = viewer.getBytesPerRow();

        // Width computations
        int charWidth = metrics.charWidth('D');
        int hexColWidth = charWidth * 3;
        int addressLineX = 12 * charWidth;
        int firstDataColumnX = addressLineX + charWidth;
        int firstAsciiColumnX = firstDataColumnX +
                (bytesPerRow * hexColWidth) + 2 * charWidth;

        // Height computations
        int rowHeight = viewer.getRowHeight();
        int firstVisibleRow = Math.max(0, clipBounds.y / rowHeight - 1);
        int lastVisibleRow = Math.max(firstVisibleRow,
                                      (clipBounds.y + clipBounds.height) / rowHeight - 1);

        int y = rowHeight * (firstVisibleRow + 2);
        long position = firstVisibleRow * bytesPerRow;

        CellRenderer renderer = viewer.getCellRenderer();
        long cursor = viewer.getSelectionModel().getCursor();
        long selectionStart = viewer.getSelectionModel().getSelectionStart();
        long selectionEnd = viewer.getSelectionModel().getSelectionEnd();

        long cursorRow = cursor / bytesPerRow;

        for (int row = firstVisibleRow;
             row <= lastVisibleRow && position < binary.length();
             row++) {

            // Background highlight for the row the cursor is on.
            if (row == cursorRow) {
                g.setColor(viewer.getCursorRowBackground());
                g.fillRect(0, row * rowHeight + rowHeight, viewer.getWidth(), rowHeight);
            }

            int rowDataLength = (int) Math.min(bytesPerRow, binary.length() - position);

            paintRow(viewer, g, position, rowDataLength, selectionStart, selectionEnd, row == cursorRow, cursor,
                     hexColWidth, charWidth, rowHeight, y,
                     addressLineX, firstDataColumnX, firstAsciiColumnX,
                     renderer);

            position += bytesPerRow;
            y += rowHeight;
        }

        // Address divider line.
        g.setColor(viewer.getOffsetForeground());
        g.setStroke(new BasicStroke(1.0f));
        g.draw(new Line2D.Float(addressLineX, rowHeight * firstVisibleRow,
                                addressLineX, rowHeight * (lastVisibleRow + 1)));
    }

    // Painting a row is split out to give IDEA a bit of a help with the inspection.

    private void paintRow(HexViewer viewer, Graphics2D g, long position, int rowDataLength,
                          long selectionStart, long selectionEnd, boolean onCursorRow, long cursor,
                          int hexColWidth, int asciiColWidth, int rowHeight, int y,
                          int addressLineX, int firstDataColumnX, int firstAsciiColumnX,
                          CellRenderer renderer) {

        Component comp;

        Graphics g2 = g.create();
        try {
            // Row offset
            comp = renderer.getRendererComponent(viewer, false, onCursorRow, false,
                                                 position, CellRenderer.ROW_OFFSET);
            comp.setBounds(asciiColWidth, y - rowHeight, addressLineX - asciiColWidth*2, rowHeight);
            g2.translate(asciiColWidth, y - rowHeight);
            comp.paint(g2);
            g2.translate(-asciiColWidth, -y + rowHeight);

            // Hex digits for this row
            int hexX = firstDataColumnX;
            int asciiX = firstAsciiColumnX;
            for (int i = 0; i < rowDataLength; i++) {

                boolean insideSelection = selectionStart <= position && selectionEnd >= position;
                boolean atCursor = position == cursor;

                // Hex column
                comp = renderer.getRendererComponent(viewer, insideSelection, onCursorRow, atCursor,
                                                     position, CellRenderer.HEX);
                comp.setBounds(hexX, y - rowHeight, hexColWidth, rowHeight);
                g2.translate(hexX, y - rowHeight);
                comp.paint(g2);
                g2.translate(-hexX, -y + rowHeight);

                // ASCII column
                comp = renderer.getRendererComponent(viewer, insideSelection, onCursorRow, atCursor,
                                                     position, CellRenderer.ASCII);
                comp.setBounds(asciiX, y - rowHeight, asciiColWidth, rowHeight);
                g2.translate(asciiX, y - rowHeight);
                comp.paint(g2);
                g2.translate(-asciiX, -y + rowHeight);

                position++;
                hexX += hexColWidth;
                asciiX += asciiColWidth;
            }
        } finally {
            g2.dispose();
        }
    }

    @Override
    public void installUI(JComponent c) {
        installKeyboardActions((HexViewer) c);
        installListeners((HexViewer) c);
    }

    protected void installKeyboardActions(HexViewer viewer) {
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        InputMap focusedMap = viewer.getInputMap(JComponent.WHEN_FOCUSED);
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "cursor-down");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "cursor-up");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "cursor-left");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "cursor-right");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), "selection-down");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), "selection-up");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), "selection-left");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), "selection-right");
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutMask), "select-all");

        // These are redundant when running inside HexFrame but I figure it will help someone.
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, shortcutMask),
                       TransferHandler.getCutAction().getValue(Action.NAME));
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcutMask),
                       TransferHandler.getCopyAction().getValue(Action.NAME));
        focusedMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, shortcutMask),
                       TransferHandler.getPasteAction().getValue(Action.NAME));

        ActionMap actions = viewer.getActionMap();
        actions.put("cursor-down", new CursorDownAction());
        actions.put("cursor-up", new CursorUpAction());
        actions.put("cursor-left", new CursorLeftAction());
        actions.put("cursor-right", new CursorRightAction());
        actions.put("selection-down", new SelectionDownAction());
        actions.put("selection-up", new SelectionUpAction());
        actions.put("selection-left", new SelectionLeftAction());
        actions.put("selection-right", new SelectionRightAction());
        actions.put("select-all", new SelectAllAction());

        actions.put(TransferHandler.getCutAction().getValue(Action.NAME),
                    TransferHandler.getCutAction());
        actions.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                    TransferHandler.getCopyAction());
        actions.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                    TransferHandler.getPasteAction());
    }

    protected void installListeners(HexViewer viewer) {
        BasicMouseAdapter mouseAdapter = new BasicMouseAdapter();
        viewer.addMouseListener(mouseAdapter);
        viewer.addMouseMotionListener(mouseAdapter);
    }


}
