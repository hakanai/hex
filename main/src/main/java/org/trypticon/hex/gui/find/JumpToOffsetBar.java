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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.text.TextAction;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.HexViewerSelectionModel;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.util.Format;
import org.trypticon.hex.util.Localisable;
import org.trypticon.hex.util.swingsupport.LocalisableComboBox;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Bar with the UI to jump to an offset.
 *
 * @author trejkaz
 */
public class JumpToOffsetBar extends JPanel {
    private final HexViewer viewer;

    private final JComboBox<JumpType> jumpTypeComboBox;
    private final CustomHexFormattedTextField offsetField;
    private final JButton jumpForwardsButton;
    private final JButton jumpBackwardsButton;

    public JumpToOffsetBar(HexViewer viewer) {
        this.viewer = viewer;

        viewer.addPropertyChangeListener("binary", event -> binaryChanged());

        jumpTypeComboBox = new LocalisableComboBox<>(Format.LONG, JumpType.values());

        offsetField = new CustomHexFormattedTextField();

        jumpForwardsButton = new JButton(Resources.getString("JumpToOffsetBar.jump"));
        jumpForwardsButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        jumpForwardsButton.putClientProperty("JButton.segmentPosition", "only");
        jumpForwardsButton.setMargin(new Insets(0, 0, 0, 0));

        jumpBackwardsButton = new JButton(Resources.getString("JumpToOffsetBar.backwards"));
        jumpBackwardsButton.putClientProperty("JButton.buttonType", "segmentedTextured");
        jumpBackwardsButton.putClientProperty("JButton.segmentPosition", "last");
        jumpBackwardsButton.setMargin(new Insets(0, 0, 0, 0));
        jumpBackwardsButton.setVisible(false);

        jumpTypeComboBox.addItemListener(event -> selectedJumpTypeChanged());
        offsetField.addActionListener(event -> jumpForwardsButton.doClick());
        jumpForwardsButton.addActionListener(event -> jumpForwards());
        jumpBackwardsButton.addActionListener(event -> jumpBackwards());

        binaryChanged();

        PLAFUtils.makeSmall(jumpTypeComboBox, offsetField, jumpForwardsButton, jumpBackwardsButton);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                                      .addGap(4)
                                      .addComponent(jumpTypeComboBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(offsetField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                      .addComponent(jumpForwardsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addComponent(jumpBackwardsButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                      .addGap(4));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(jumpTypeComboBox)
                                    .addComponent(offsetField)
                                    .addComponent(jumpForwardsButton)
                                    .addComponent(jumpBackwardsButton));
    }

    private void binaryChanged() {
        Binary binary = viewer.getBinary();
        long maxLength = binary != null ? binary.length() : 1;
        offsetField.updateMaxValue(maxLength);
        revalidate();
    }

    private void selectedJumpTypeChanged() {
        JumpType jumpType = (JumpType) jumpTypeComboBox.getSelectedItem();
        if (jumpType == JumpType.ABSOLUTE) {
            jumpForwardsButton.setText(Resources.getString("JumpToOffsetBar.jump"));
            jumpForwardsButton.putClientProperty("JButton.segmentPosition", "only");
            jumpBackwardsButton.setVisible(false);
        } else {
            jumpForwardsButton.setText(Resources.getString("JumpToOffsetBar.forwards"));
            jumpForwardsButton.putClientProperty("JButton.segmentPosition", "first");
            jumpBackwardsButton.setVisible(true);
        }
    }

    private void jumpForwards() {
        jump(1);
    }

    private void jumpBackwards() {
        jump(-1);
    }

    private void jump(int sign) {
        Number offsetValue = (Number) offsetField.getValue();
        if (offsetValue == null) {
            return;
        }

        long offset = offsetValue.longValue() * sign;
        HexViewerSelectionModel selectionModel = viewer.getSelectionModel();

        switch ((JumpType) jumpTypeComboBox.getSelectedItem()) {
            case ABSOLUTE:
                // Use the offset we got.
                break;

            case RELATIVE_TO_CURSOR:
                offset = selectionModel.getCursor() + offset;
                break;

            case RELATIVE_TO_SUB_REGION: {
                List<? extends Annotation> path =
                    viewer.getAnnotations().getAnnotationPathAt(selectionModel.getCursor());
                if (path == null) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                path = new ArrayList<>(path);
                if (!path.isEmpty() && !(path.get(path.size() - 1) instanceof GroupAnnotation)) {
                    path.remove(path.size() - 1);
                    // We just removed the deepest non-group annotation so the deepest annotation
                    // in the list must now be a group annotation.
                }
                if (path.isEmpty()) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                GroupAnnotation deepestGroup = (GroupAnnotation) path.get(path.size() - 1);
                offset = deepestGroup.getPosition() + offset;
                break;
            }
        }

        if (offset >= 0 && offset < viewer.getBinary().length()) {
            selectionModel.setCursor(offset);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        // Redirect focus to the text field.
        return offsetField.requestFocusInWindow();
    }

    //TODO: Later, pull out common bits from similar class over in Hex Components.
    // Non-trivial because one is a StealthFormattedTextField and the other is not.
    private static class CustomHexFormattedTextField extends JFormattedTextField {
        private long maxValue = 1;

        // setFormatter() appears to have no effect. Swing uses its own default number formatter.
        CustomHexFormattedTextField() {
            super(new AbstractFormatterFactory() {
                @Override
                public AbstractFormatter getFormatter(JFormattedTextField tf) {
                    return new AbstractFormatter() {
                        @Override
                        public Object stringToValue(String text) throws ParseException {
                            text = text.trim();
                            if (text.isEmpty()) {
                                return null;
                            }
                            try {
                                return Long.decode(text);
                            } catch (NumberFormatException e) {
                                throw new ParseException(text, 0);
                            }
                        }

                        @Override
                        public String valueToString(Object value) throws ParseException {
                            if (value == null) {
                                return "";
                            }
                            return "0x" + Long.toString(((Number) value).longValue(), 16); //NON-NLS
                        }
                    };
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            if (isPreferredSizeSet()) {
                return size;
            }

            // JTextField's columns are computed in terms of 'm' which makes it too wide on some fonts.
            StringBuilder longestString;
            try {
                longestString = new StringBuilder(getFormatter().valueToString(maxValue));
            } catch (ParseException e) {
                throw new RuntimeException("Unexpected error converting to string", e);
            }

            FontMetrics metrics = getFontMetrics(getFont());

            int widthOfWidestDigit = 0;
            for (int value = 0; value < 16; value++) {
                String string;
                try {
                    string = getFormatter().valueToString(value);
                } catch (ParseException e) {
                    throw new RuntimeException("Unexpected error converting to string", e);
                }
                string = string.substring(2); // chopping off "0x"
                widthOfWidestDigit = Math.max(widthOfWidestDigit, metrics.stringWidth(string));
            }

            int numDigits = longestString.length() - 2; // chopping off "0x"
            // The widthOfWidestDigit / 2 here is to give breathing room for placing the caret after the value.
            int longestValueWidth = metrics.stringWidth("0x") + numDigits * widthOfWidestDigit + widthOfWidestDigit / 2; //NON-NLS

            Insets insets = getInsets();
            size.width = longestValueWidth + insets.left + insets.right;
            return size;
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setActionMap(new TrickyActionMap(super.getActionMap()));
        }

        private void updateMaxValue(long maxValue) {
            this.maxValue = maxValue;
            invalidate();
        }

        private static class TrickyActionMap extends ActionMap {
            public TrickyActionMap(ActionMap realActionMap) {
                setParent(realActionMap);
            }

            @Override
            public Action get(Object key) {
                Action actualAction = super.get(key);
                if ("notify-field-accept".equals(key) && !(actualAction instanceof AlwaysEnabledAction)) {
                    actualAction = new AlwaysEnabledAction(actualAction);
                    // Updating the real map reduces the number of instances floating around if
                    // this method gets called more than once for the same key.
                    put("notify-field-accept", actualAction);
                }
                return actualAction;
            }
        }

        // Wraps the real action and pretends to be enabled at all times.
        private static class AlwaysEnabledAction extends TextAction {
            private final Action delegate;

            private AlwaysEnabledAction(Action delegate) {
                super((String) delegate.getValue(NAME));
                this.delegate = delegate;
            }

            @Override
            public void actionPerformed(ActionEvent event) {
                delegate.actionPerformed(event);
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        }

    }

    private static enum JumpType implements Localisable {
        ABSOLUTE("JumpToOffsetBar.absolute"),
        RELATIVE_TO_CURSOR("JumpToOffsetBar.relativeToCursor"),
        RELATIVE_TO_SUB_REGION("JumpToOffsetBar.relativeToSubRegion");

        private final String stringKey;

        private JumpType(String stringKey) {
            this.stringKey = stringKey;
        }

        @Override
        public String toLocalisedString(Format style) {
            return toLocalisedString(style, Locale.getDefault(Locale.Category.DISPLAY));
        }

        @Override
        public String toLocalisedString(Format style, Locale locale) {
            return Resources.getString(stringKey);
        }
    }
}
