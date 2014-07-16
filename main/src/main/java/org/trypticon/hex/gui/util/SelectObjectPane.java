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

package org.trypticon.hex.gui.util;

import java.awt.Component;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.renderer.DefaultListRenderer;
import org.jdesktop.swingx.renderer.StringValue;

/**
 * Pane allows selecting an object as an alternative to things like menus which can
 * often be too large to navigate.
 *
 * @param <E> the type of object being selected.
 * @author trejkaz
 */
public abstract class SelectObjectPane<E> extends ValidatingPanel {
    private final JTextField textField;
    private final JXList list;

    protected SelectObjectPane() {
        textField = new JTextField();
        textField.selectAll();
        textField.setColumns(30);
        textField.setBorder(BorderFactory.createEtchedBorder());
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                updateFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                updateFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                updateFilter();
            }
        });

        DefaultListModel<E> listModel = new DefaultListModel<>();
        createList().forEach(listModel::addElement);

        list = new JXList(listModel);
        list.setCellRenderer(new DefaultListRenderer(createDisplayConverter()));
        list.setAutoCreateRowSorter(true);
        list.setVisibleRowCount(8);
        JScrollPane listScroll = new JScrollPane(list);
        list.addListSelectionListener(event -> updateInputValid());
        list.getRowSorter().addRowSorterListener(event -> updateInputValid());

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup()
                                        .addComponent(textField)
                                        .addComponent(listScroll));

        layout.setVerticalGroup(layout.createSequentialGroup()
                                      .addComponent(textField)
                                      .addComponent(listScroll));
    }

    /**
     * Creates the collection of objects which can be selected.
     *
     * @return the selectable objects.
     */
    protected abstract List<E> createList();

    /**
     * Creates the string value converter used to render the values.
     *
     * @return the display converter.
     */
    protected abstract StringValue createDisplayConverter();

    /**
     * Creates the filter predicate for the given filter text.
     *
     * @param filterText the filter text.
     * @return the predicate.
     */
    protected abstract Predicate<E> createFilterPredicate(String filterText);

    @Override
    protected boolean validateInput() {
        // Either there is only one matching row, or there is one selected row.
        return list.getRowSorter().getViewRowCount() == 1 ||
               list.getSelectedIndices().length == 1;
    }

    /**
     * Shows the annotation pane in a dialog.
     *
     * @param parentComponent the parent component.
     * @return the chosen interpreter.
     */
    protected E showDialog(Component parentComponent, String title, String okButtonText) {
        if (OptionPanes.showInputDialog(parentComponent, this, textField,
                                        title, okButtonText,
                                        UIManager.getString("OptionPane.cancelButtonText"))) {
            int modelIndex;
            if (list.getRowSorter().getViewRowCount() == 1) {
                modelIndex = list.convertIndexToModel(0);
            } else {
                modelIndex = list.convertIndexToModel(list.getSelectedIndex());
            }
            return safeCast(list.getModel().getElementAt(modelIndex));
        } else {
            return null;
        }
    }

    /**
     * Updates the filter on the list.
     */
    private void updateFilter() {
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            list.setRowFilter(null);
        } else {
            Predicate<E> filterPredicate = createFilterPredicate(text);
            list.setRowFilter(new RowFilter<ListModel, Integer>() {
                @Override
                public boolean include(Entry<? extends ListModel, ? extends Integer> entry) {
                    return filterPredicate.test(safeCast(entry.getValue(0)));
                }
            });
        }
    }

    /**
     * Workaround for SwingX not having the generics which should be present on {@link ListModel}.
     *
     * @param object the object to cast.
     * @return the object as an {@code E}.
     */
    @SuppressWarnings("unchecked")
    private E safeCast(Object object) {
        return (E) object;
    }
}
