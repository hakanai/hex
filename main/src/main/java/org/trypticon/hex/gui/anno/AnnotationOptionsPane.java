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

import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.util.Format;
import org.trypticon.hex.util.swingsupport.SelectEncodingButton;
import org.trypticon.hex.util.swingsupport.ValidatingPanel;

/**
 * Pane to show the options for an annotation before it is created.
 *
 * @author trejkaz
 */
public class AnnotationOptionsPane extends ValidatingPanel {
    private final Map<String, Supplier<?>> fieldValueSuppliers = new LinkedHashMap<>();
    private final Map<String, Boolean> required = new LinkedHashMap<>();

    public AnnotationOptionsPane(List<InterpreterInfo.Option<?>> options) {
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        setLayout(layout);

        JLabel explanationLabel = new JLabel(Resources.getString("AddAnnotation.optionsExplanation"));

        GroupLayout.ParallelGroup labelsGroup = layout.createParallelGroup();
        GroupLayout.ParallelGroup fieldsGroup = layout.createParallelGroup();
        GroupLayout.SequentialGroup rowsGroup = layout.createSequentialGroup();

        rowsGroup.addContainerGap()
                 .addComponent(explanationLabel)
                 .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);

        for (InterpreterInfo.Option option : options) {
            JLabel label = new JLabel(Resources.getString("AddAnnotation.optionFieldFormat",
                                                          option.toLocalisedString(Format.LONG)));
            labelsGroup.addComponent(label);

            JComponent field = createField(option.getType());
            Supplier<?> fieldValueSupplier = createFieldValueSupplier(field, option.getType());
            fieldsGroup.addComponent(field);
            fieldValueSuppliers.put(option.getName(), fieldValueSupplier);

            rowsGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                     .addComponent(label)
                                     .addComponent(field));

            if (option.isRequired()) {
                required.put(option.getName(), true);
            }
        }

        rowsGroup.addContainerGap();

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(explanationLabel)
                                        .addGroup(layout.createSequentialGroup()
                                                        .addGroup(labelsGroup)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(fieldsGroup)));
        layout.setVerticalGroup(rowsGroup);
    }

    @Override
    protected boolean validateInput() {
        for (String key : required.keySet()) {
            Object value = fieldValueSuppliers.get(key).get();
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                return false;
            }
        }
        return true;
    }

    private JComponent createField(Class<?> type) {
        if (type == Charset.class) {
            return new SelectEncodingButton();
        } else if (Integer.class == type) {
            return new JFormattedTextField(NumberFormat.getIntegerInstance());
        } else if (String.class == type) {
            return new JTextField(20);
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    private <T> Supplier<T> createFieldValueSupplier(JComponent field, Class<T> type) {
        if (type == Charset.class) {
            return () -> type.cast(((SelectEncodingButton) field).getEncoding());
        } else if (type == Integer.class) {
            return () -> type.cast(((Number) ((JFormattedTextField) field).getValue()).intValue());
        } else if (type == String.class) {
            return () -> type.cast(((JTextField) field).getText().trim());
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }

    /**
     * Populates the map of options.
     *
     * @param optionMap the map to populate.
     */
    public void populateOptionMap(Map<String, Object> optionMap) {
        fieldValueSuppliers.entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            Object value = entry.getValue().get();
            if (value != null) {
                optionMap.put(key, value);
            }
        });
    }
}
