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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.SystemColor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.UIManager;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;

import org.trypticon.hex.gui.util.Dialogs;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * A button to pick a colour.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class ColorPickerButton extends JButton {
    private final AbstractColorChooserPanel chooserPanel;

    private Color color = Color.BLACK;

    public ColorPickerButton() {
        setIcon(new WellIcon());

        //XXX: A "well" button type would be nice. Quaqua had it, but Aqua doesn't. :(
        putClientProperty("JButton.buttonType", "square");

        addActionListener(event -> buttonPressed());

        // JColorChooser itself looks like crap, but we can use bits of it to allow choosing
        // a sufficient number of colours.
        JColorChooser chooser = new JColorChooser(getBackground());
        chooserPanel = ColorChooserComponentFactory.getDefaultChooserPanels()[0];
        chooserPanel.installChooserPanel(chooser);
        ColorSelectionModel chooserModel = chooserPanel.getColorSelectionModel();
        chooserModel.addChangeListener(event -> setColor(chooserModel.getSelectedColor()));
    }

    /**
     * Gets the current selected colour.
     *
     * @return the current selected colour.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the current selected colour.
     *
     * @param color the current selected colour.
     */
    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        firePropertyChange("color", oldColor, color);
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (PLAFUtils.isAqua()) {
            setMargin(new Insets(4, 2, 5, 3)); // visually 4,4,4,4 on all sides
        } else {
            setMargin(new Insets(4, 4, 4, 4));
        }
    }

    private void buttonPressed() {
        Dialogs.popupAutoClosingModalDialog(this, chooserPanel, new Point(0, getHeight()));
    }

    private class WellIcon implements Icon {
        private static final int WIDTH = 34;
        private static final int HEIGHT = 13;

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, WIDTH, HEIGHT);
            g.setColor(c.isEnabled() ? SystemColor.controlShadow : UIManager.getColor("Button.background"));
            g.drawRect(x, y, WIDTH, HEIGHT);
        }

        @Override
        public int getIconWidth() {
            return WIDTH;
        }

        @Override
        public int getIconHeight() {
            return HEIGHT;
        }
    }
}
