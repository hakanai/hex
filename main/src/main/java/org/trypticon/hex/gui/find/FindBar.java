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

import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Bar allowing searching the binary.
 *
 * @author trejkaz
 */
public class FindBar extends JPanel {
    private final HexViewer viewer;

    private final JToggleButton hexButton;
    private final JToggleButton textButton;
    private final HexTextField hexTextField;
    private final JTextField textField;
    private final SelectEncodingButton encodingButton;
    private final JButton previousButton;
    private final JButton nextButton;

    public FindBar(HexViewer viewer) {
        this.viewer = viewer;

        hexButton = new JToggleButton(Resources.getString("FindBar.hex"), true);
        hexButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        hexButton.putClientProperty("JButton.segmentPosition", "first");
        hexButton.setMargin(new Insets(0, 0, 0, 0));
        hexButton.addActionListener(event -> switchToHex());

        textButton = new JToggleButton(Resources.getString("FindBar.text"));
        textButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        textButton.putClientProperty("JButton.segmentPosition", "last");
        textButton.setMargin(new Insets(0, 0, 0, 0));
        textButton.addActionListener(event -> switchToText());

        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(hexButton);
        modeButtonGroup.add(textButton);

        hexTextField = new HexTextField("", 40);
        hexTextField.addActionListener(event -> findNext());
        textField = new JTextField("", 40);
        textField.addActionListener(event -> findNext());

        encodingButton = new SelectEncodingButton();
        encodingButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        encodingButton.putClientProperty("JButton.segmentPosition", "only");
        encodingButton.setMargin(new Insets(0, 0, 0, 0));

        previousButton = new JButton(Resources.getString("FindBar.previous"));
        previousButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        previousButton.putClientProperty("JButton.segmentPosition", "first");
        previousButton.setMargin(new Insets(0, 0, 0, 0));
        previousButton.addActionListener(event -> findPrevious());

        nextButton = new JButton(Resources.getString("FindBar.next"));
        nextButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        nextButton.putClientProperty("JButton.segmentPosition", "last");
        nextButton.setMargin(new Insets(0, 0, 0, 0));
        nextButton.addActionListener(event -> findNext());

        PLAFUtils.makeSmall(this, hexButton, textButton, hexTextField, textField,
                            encodingButton, previousButton, nextButton);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                                      .addGap(4)
                                      .addComponent(hexButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addComponent(textButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(hexTextField)
                                      .addComponent(textField)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(encodingButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(previousButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addComponent(nextButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addGap(4));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(hexButton)
                                    .addComponent(textButton)
                                    .addComponent(hexTextField)
                                    .addComponent(textField)
                                    .addComponent(encodingButton)
                                    .addComponent(previousButton)
                                    .addComponent(nextButton));

        switchToHex();
    }

    private void switchToHex() {
        encodingButton.setVisible(false);
        textField.setVisible(false);
        hexTextField.setVisible(true);
        hexTextField.requestFocusInWindow();
    }

    private void switchToText() {
        encodingButton.setVisible(true);
        hexTextField.setVisible(false);
        textField.setVisible(true);
        textField.requestFocusInWindow();
    }

    private void findNext() {
        find(false);
    }

    private void findPrevious() {
        find(true);
    }

    private void find(boolean backwards) {
        Searcher searcher;

        if (hexButton.isSelected()) {
            searcher = new BinarySearcher(hexTextField.getBinary());
        } else {
            searcher = new TextSearcher(textField.getText(), encodingButton.getEncoding());
        }

        //XXX: Option not to wrap? Doesn't seem particularly crucial.
        SearchParams params = new SearchParams(true, backwards);

        Match match = searcher.find(viewer.getBinary(), viewer.getSelectionModel().getCursor(), params);
        if (match != null) {
            viewer.getSelectionModel().setSelection(match.offset, match.endOffset());
        }
    }
}
