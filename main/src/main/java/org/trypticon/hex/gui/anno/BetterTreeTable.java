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

package org.trypticon.hex.gui.anno;

import javax.annotation.Nullable;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * Workarounds for issues in {@link JXTreeTable}.
 *
 * @author trejkaz
 */
public class BetterTreeTable extends JXTreeTable {

    @Nullable
    private Highlighter oddRowHighlighter;

    @Override
    public void updateUI() {
        if (oddRowHighlighter != null) {
            removeHighlighter(oddRowHighlighter);
            oddRowHighlighter = null;
        }

        super.updateUI();

        // JTable does this striping automatically but JXTable's renderer seems to break it, so JXTreeTable
        // inherits this broken behaviour.
        if (UIManager.get("Table.alternateRowColor") != null) {
            oddRowHighlighter =
                HighlighterFactory.createSimpleStriping(UIManager.getColor("Table.alternateRowColor"));
            addHighlighter(oddRowHighlighter);
        }
    }
}
