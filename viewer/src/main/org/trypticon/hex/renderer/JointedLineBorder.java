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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 * A Swing border similar to {@code LineBorder} but which can paint only some
 * edges or corners.  It also has zero insets, to ensure that when painted on
 * a label, it does not steal space from painting the character, if the label is
 * exactly the right size to paint the character.
 *
 * @author trejkaz
 */
public class JointedLineBorder implements Border {
    private final Color colour;
    private final boolean top;
    private final boolean right;
    private final boolean bottom;
    private final boolean left;

    public JointedLineBorder(Color colour, boolean top, boolean right, boolean bottom, boolean left) {
        this.colour = colour;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        graphics.setColor(colour);
        if (top) {
            graphics.drawLine(x, y, width - 1, y);
        }
        if (bottom) {
            graphics.drawLine(x, y + height - 1, width - 1, y + height - 1);
        }
        if (left) {
            graphics.drawLine(x, 0, x, height - 1);
        }
        if (right) {
            graphics.drawLine(x + width - 1, 0, x + width - 1, height - 1);
        }
    }

    public Insets getBorderInsets(Component component) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
