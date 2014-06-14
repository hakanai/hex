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

import java.awt.Component;
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

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.OptionPanes;
import org.trypticon.hex.gui.util.Strings;
import org.trypticon.hex.gui.util.ValidatingPanel;
import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.interpreters.MasterInterpreterStorage;
import org.trypticon.hex.util.Format;

/**
 * Pane allows selecting an annotation to add.
 */
public class AddAnnotationPane extends ValidatingPanel {
    private final JTextField textField;
    private final JXList list;

    public AddAnnotationPane() {
        textField = new JTextField() {
            @Override
            public void addNotify() {
                super.addNotify();
                textField.requestFocus();
            }
        };
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

        DefaultListModel<InterpreterInfo> listModel = new DefaultListModel<>();
        new MasterInterpreterStorage().getInterpreterInfos().forEach(listModel::addElement);
        list = new JXList(listModel);
        list.setCellRenderer(new DefaultListRenderer(info -> ((InterpreterInfo) info).toLocalisedString(Format.LONG)));
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
    public InterpreterInfo showDialog(Component parentComponent) {
        if (OptionPanes.showInputDialog(parentComponent, this, textField,
                                        Resources.getString("AddAnnotation.nameWithoutEllipsis"),
                                        Resources.getString("AddAnnotation.okButton"),
                                        UIManager.getString("OptionPane.cancelButtonText"))) {
            int modelIndex;
            if (list.getRowSorter().getViewRowCount() == 1) {
                modelIndex = list.convertIndexToModel(0);
            } else {
                modelIndex = list.getSelectedIndex();
            }
            return (InterpreterInfo) list.getModel().getElementAt(modelIndex);
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
            final String[] textFragments = Strings.splitOnWhitespace(text);
            list.setRowFilter(new RowFilter<ListModel, Integer>() {
                @Override
                public boolean include(Entry<? extends ListModel, ? extends Integer> entry) {
                    InterpreterInfo info = (InterpreterInfo) entry.getValue(0);
                    String shortName = info.toLocalisedString(Format.SHORT);
                    String longName = info.toLocalisedString(Format.LONG);
                    for (String fragment : textFragments) {
                        if (!Strings.containsIgnoreCase(shortName, fragment) &&
                            !Strings.containsIgnoreCase(longName, fragment)) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }
    }

}
