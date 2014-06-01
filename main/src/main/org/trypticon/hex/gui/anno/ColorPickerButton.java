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

import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorChooserComponentFactory;
import javax.swing.colorchooser.ColorSelectionModel;

import org.trypticon.hex.gui.util.Dialogs;

/**
 * A button to pick a colour.
 *
 * @author trejkaz
 */
public class ColorPickerButton extends JButton {
    private final AbstractColorChooserPanel chooserPanel;

    public ColorPickerButton() {
        setText("          ");

        // Aqua look and feel sets buttons to non-opaque by default which causes the background
        // colour not to be painted.
        setOpaque(true);

        //XXX: A "well" button type would be nice. Quaqua had it, but Aqua doesn't. :(
        putClientProperty("JButton.buttonType", "square");

        addActionListener(event -> buttonPressed());

        // JColorChooser itself looks like crap, but we can use bits of it to allow choosing
        // a sufficient number of colours.
        JColorChooser chooser = new JColorChooser(getBackground());
        chooserPanel = ColorChooserComponentFactory.getDefaultChooserPanels()[0];
        chooserPanel.installChooserPanel(chooser);
        ColorSelectionModel chooserModel = chooserPanel.getColorSelectionModel();
        chooserModel.addChangeListener(event -> setBackground(chooserModel.getSelectedColor()));
    }

    private void buttonPressed() {
        Dialogs.popupAutoClosingModalDialog(this, chooserPanel, new Point(0, getHeight()));
    }
}
