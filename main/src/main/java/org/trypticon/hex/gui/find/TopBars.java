/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
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

package org.trypticon.hex.gui.find;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXCollapsiblePane;

import org.trypticon.hex.HexViewer;

/**
 * Container for any bars which appear at the top.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class TopBars extends JPanel {
    private final JXCollapsiblePane collapser;

    private final FindBar findBar;
    private final JumpToOffsetBar jumpToOffsetBar;

    public TopBars(HexViewer viewer) {
        findBar = new FindBar(viewer);
        jumpToOffsetBar = new JumpToOffsetBar(viewer);

        collapser = new JXCollapsiblePane(JXCollapsiblePane.Direction.START);
        collapser.setCollapsed(true);
        setLayout(new BorderLayout());
        add(collapser, BorderLayout.CENTER);
    }

    /**
     * Gets the find bar.
     *
     * @return the find bar.
     */
    public FindBar getFindBar() {
        return findBar;
    }

    /**
     * Shows the find bar.
     */
    public void showFindBar() {
        showBar(findBar);
    }

    /**
     * Shows the jump to offset bar.
     */
    public void showJumpToOffsetBar() {
        showBar(jumpToOffsetBar);
    }

    private void showBar(JPanel bar) {
        if (!collapser.isCollapsed()) {
            if (collapser.getContentPane() != bar) {
                collapser.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        collapser.removePropertyChangeListener("collapsed", this);
                        showBar(bar);
                    }
                });
                collapser.setCollapsed(true);
            } else {
                // Already visible, but we want to return focus to it.
                bar.requestFocusInWindow();
            }
            return;
        }
        collapser.setContentPane(bar);
        collapser.setCollapsed(false);
        bar.requestFocusInWindow();
    }

    /**
     * Hides whatever bar is currently visible.
     */
    public void hideBar() {
        collapser.setCollapsed(true);
    }
}
