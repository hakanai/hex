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
import java.awt.event.ItemEvent;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.trypticon.hex.gui.Resources;

/**
 * Pane for editing parametric style values.
 */
public class ParametricStyleEditorPane extends JPanel {
    private final JCheckBox customCheckBox;
    private final JLabel borderStrokeStyleLabel;
    private final JComboBox<ParametricStyle.StrokeStyle> borderStrokeStyleComboBox;
    private final JLabel borderColourLabel;
    private final ColourPickerButton borderColourButton;
    private final JLabel backgroundColourLabel;
    private final ColourPickerButton backgroundColourButton;

    // State variables.
    private boolean custom;

    @NotNull
    private ParametricStyle workingStyle;

    public ParametricStyleEditorPane(@Nullable ParametricStyle style) {
        if (style != null) {
            workingStyle = style;
        } else {
            workingStyle = new ParametricStyle(ParametricStyle.StrokeStyle.SOLID,
                                               Color.BLACK, Color.WHITE);
        }

        customCheckBox = new JCheckBox(Resources.getString("ParametricStyleEditor.overrideDefaultStyle"));
        borderStrokeStyleLabel = new JLabel(Resources.getString("ParametricStyleEditor.borderStyle"));
        borderStrokeStyleComboBox = new JComboBox<>(ParametricStyle.StrokeStyle.values());
        borderColourLabel = new JLabel(Resources.getString("ParametricStyleEditor.borderColour"));
        borderColourButton = new ColourPickerButton();
        backgroundColourLabel = new JLabel(Resources.getString("ParametricStyleEditor.backgroundColour"));
        backgroundColourButton = new ColourPickerButton();

        borderStrokeStyleComboBox.setSelectedItem(workingStyle.getBorderStrokeStyle());
        borderColourButton.setBackground(workingStyle.getBorderColor());
        backgroundColourButton.setBackground(workingStyle.getBackgroundColor());

        customCheckBox.addItemListener(event -> {
            setCustom(event.getStateChange() == ItemEvent.SELECTED);
        });
        borderStrokeStyleComboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                setWorkingStyle(new ParametricStyle((ParametricStyle.StrokeStyle) event.getItem(),
                                                    workingStyle.getBorderColor(),
                                                    workingStyle.getBackgroundColor()));
            }
        });
        borderColourButton.addPropertyChangeListener("background", event -> {
            setWorkingStyle(new ParametricStyle(workingStyle.getBorderStrokeStyle(),
                                                (Color) event.getNewValue(),
                                                workingStyle.getBackgroundColor()));
        });
        backgroundColourButton.addPropertyChangeListener("background", event -> {
            setWorkingStyle(new ParametricStyle(workingStyle.getBorderStrokeStyle(),
                                                workingStyle.getBorderColor(),
                                                (Color) event.getNewValue()));
        });

        syncEnabledState();
        customCheckBox.addItemListener(event -> syncEnabledState());

        if (style != null) {
            customCheckBox.setSelected(true);
            borderStrokeStyleComboBox.setSelectedItem(style.getBorderStrokeStyle());
            borderColourButton.setBackground(style.getBorderColor());
            backgroundColourButton.setBackground(style.getBackgroundColor());
        }

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                          .addComponent(borderStrokeStyleLabel)
                          .addComponent(borderColourLabel)
                          .addComponent(backgroundColourLabel))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                          .addComponent(customCheckBox)
                          .addComponent(borderStrokeStyleComboBox)
                          .addComponent(borderColourButton)
                          .addComponent(backgroundColourButton)));

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(customCheckBox)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(borderStrokeStyleLabel)
                .addComponent(borderStrokeStyleComboBox))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(borderColourLabel)
                .addComponent(borderColourButton))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(backgroundColourLabel)
                .addComponent(backgroundColourButton)));
    }

    private void syncEnabledState() {
        boolean selected = customCheckBox.isSelected();

        borderStrokeStyleLabel.setEnabled(selected);
        borderStrokeStyleComboBox.setEnabled(selected);

        borderColourLabel.setEnabled(selected);
        borderColourButton.setEnabled(selected);
        borderColourButton.setOpaque(selected);

        backgroundColourLabel.setEnabled(selected);
        backgroundColourButton.setEnabled(selected);
        backgroundColourButton.setOpaque(selected);
    }

    /**
     * Gets the current style shown in the pane.
     * This is a JavaBeans bound property.
     *
     * @return the style.
     */
    @Nullable
    public ParametricStyle getStyle() {
        return custom ? workingStyle : null;
    }

    private void setCustom(boolean custom) {
        ParametricStyle oldStyle = getStyle();
        this.custom = custom;
        firePropertyChange("style", oldStyle, getStyle());
    }

    private void setWorkingStyle(@NotNull ParametricStyle workingStyle) {
        ParametricStyle oldStyle = getStyle();
        this.workingStyle = workingStyle;
        firePropertyChange("style", oldStyle, getStyle());
    }
}
