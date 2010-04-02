/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.LinkedList;
import java.util.List;

/**
 * A border composed of multiple borders, each drawn on top of the other.
 *
 * @author trejkaz
 */
public class StackedBorder extends AbstractBorder {
    private final List<Border> borders = new LinkedList<Border>();

    public StackedBorder(Border firstBorder) {
        borders.add(firstBorder);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        for (Border border : borders) {
            border.paintBorder(c, g, x, y, width, height);
        }
    }

    public void stack(Border border) {
        borders.add(border);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return borders.get(0).getBorderInsets(c);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return borders.get(0).getBorderInsets(c);
    }

    @Override
    public boolean isBorderOpaque() {
        return borders.get(0).isBorderOpaque();
    }
}
