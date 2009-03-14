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
import java.awt.Color;
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

import org.trypticon.binary.Binary;
import org.trypticon.hex.HexUtils;
import org.trypticon.hex.HexViewer;

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
        return new Rectangle(xFixed + (12 * charWidth + charWidth / 2),
                             yFixed + rowHeight,
                             3 * charWidth,
                             rowHeight);
    }

    @Override
    public long viewToModel(HexViewer viewer, Point point) {
        int charWidth = viewer.getFontMetrics(viewer.getFont()).charWidth('D');
        int rowHeight = viewer.getRowHeight();

        // Recompute X and Y values relative to where we actually paint the
        // highlight.
        long binaryLength = viewer.getBinary().length();
        int xFixed = point.x - (12 * charWidth + charWidth / 2);
        int yFixed = point.y - rowHeight;

        int bytesX = xFixed / (3 * charWidth);
        int bytesY = yFixed / rowHeight;

        int maxBytesX = viewer.getBytesPerRow() - 1;
        long maxBytesY = (int) (binaryLength / viewer.getBytesPerRow());

        bytesX = Math.max(0, Math.min(maxBytesX, bytesX));
        bytesY = Math.max(0, (int) Math.min(maxBytesY, bytesY));

        long pos = (long) bytesY * viewer.getBytesPerRow() + bytesX;
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
        int addressLineX = hexColWidth + 9 * charWidth;
        int firstDataColumnX = addressLineX + charWidth;
        int firstAsciiColumnX = firstDataColumnX +
                (bytesPerRow * hexColWidth) + 2 * charWidth;

        // Height computations
        int rowHeight = viewer.getRowHeight();
        int firstVisibleRow = Math.max(0, clipBounds.y / rowHeight - 1);
        int lastVisibleRow = Math.max(firstVisibleRow,
                                      (clipBounds.y + clipBounds.height) / rowHeight - 1);
        int charYOffset = (rowHeight - metrics.getAscent()) / 2;

        int y = rowHeight * (firstVisibleRow + 2);
        long position = firstVisibleRow * bytesPerRow;

        Color foreground = viewer.getForeground();
        Color offsetForeground = viewer.getOffsetForeground();
        Color selectionForeground = viewer.getSelectionForeground();
        if (selectionForeground == null) {
            selectionForeground = foreground;
        }
        Color selectionBackground = viewer.getSelectionBackground();
        Color cursorForeground = viewer.getCursorForeground();
        if (cursorForeground == null) {
            cursorForeground = foreground;
        }
        Color cursorBackground = viewer.getCursorBackground();
        long cursor = viewer.getSelectionModel().getCursor();
        long selectionStart = viewer.getSelectionModel().getSelectionStart();
        long selectionEnd = viewer.getSelectionModel().getSelectionEnd();

        long cursorRow = cursor / bytesPerRow;
        byte[] rowData = new byte[bytesPerRow];

        for (int row = firstVisibleRow;
             row <= lastVisibleRow && position < binary.length();
             row++) {

            // Background highlight for the row the cursor is on.
            if (row == cursorRow) {
                g.setColor(viewer.getCursorRowBackground());
                g.fillRect(0, row * rowHeight + rowHeight, viewer.getWidth(), rowHeight);
            }

            int rowDataLength = (int) Math.min(bytesPerRow, binary.length() - position);
            binary.read(position, rowData, 0, rowDataLength);

            paintRow(g, position, rowData, rowDataLength, selectionStart, selectionEnd, cursor,
                     hexColWidth, charWidth, rowHeight, y, charYOffset,
                     firstDataColumnX, firstAsciiColumnX,
                     foreground, offsetForeground, cursorBackground, cursorForeground,
                     selectionBackground, selectionForeground);

            position += bytesPerRow;
            y += rowHeight;
        }

        // Address divider line.
        g.setColor(offsetForeground);
        g.setStroke(new BasicStroke(1.0f));
        g.draw(new Line2D.Float(addressLineX, rowHeight * firstVisibleRow,
                                addressLineX, rowHeight * (lastVisibleRow + 1)));
    }

    // Painting a row is split out to give IDEA a bit of a help with the inspection.

    // TODO: Rather than doing this, I want to do some kind of two-step approach where cells are converted
    // into drawing instruction objects and then rendered.  What I haven't solved yet is how to avoid
    // allocating a large number of objects when doing this.  Maybe that doesn't matter so much anymore
    // as Java seems to be calling paint fairly infrequently these days.

    private void paintRow(Graphics2D g, long position, byte[] rowData, int rowDataLength,
                          long selectionStart, long selectionEnd, long cursor,
                          int hexColWidth, int asciiColWidth, int rowHeight, int y, int charYOffset,
                          int firstDataColumnX, int firstAsciiColumnX,
                          Color foreground, Color offsetForeground, Color cursorBackground, Color cursorForeground,
                          Color selectionBackground, Color selectionForeground) {

        // Address
        g.setColor(offsetForeground);
        g.drawString(String.format("%08x", position), hexColWidth, y);

        // Hex digits for this row
        int hexX = firstDataColumnX;
        int asciiX = firstAsciiColumnX;
        for (int i = 0; i < rowDataLength; i++) {
            byte b = rowData[i];

            boolean insideSelection = selectionStart <= position && selectionEnd >= position;
            boolean atCursor = position == cursor;


            // XXX: I would like to shift the x offset so that the cell starts at an even multiple
            // and the text is centred in that, instead of shifting the cell half a character back.

            if (atCursor) {
                g.setColor(cursorBackground);
                g.fillRect(hexX - hexColWidth / 6, y - rowHeight, hexColWidth, rowHeight);
                g.fillRect(asciiX, y - rowHeight, asciiColWidth, rowHeight);
                g.setColor(cursorForeground);
            } else if (insideSelection) {
                g.setColor(selectionBackground);
                g.fillRect(hexX - hexColWidth / 6, y - rowHeight, hexColWidth, rowHeight);
                g.fillRect(asciiX, y - rowHeight, asciiColWidth, rowHeight);
                g.setColor(selectionForeground);
            } else {
                g.setColor(foreground);
            }

            g.drawString(HexUtils.toHex(b), hexX, y - charYOffset);
            g.drawString(HexUtils.toAscii(b), asciiX, y - charYOffset);

            position++;
            hexX += hexColWidth;
            asciiX += asciiColWidth;
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
