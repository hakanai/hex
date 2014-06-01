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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;

import org.trypticon.hex.AnnotationStyleScheme;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.gui.util.Dialogs;

/**
 * Table cell editor for custom style values.
 *
 * @author trejkaz
 */
class ParametricStyleEditor extends AbstractCellEditor implements TableCellEditor {
    private final ParametricStyleRendererComponent component;

    private ParametricStyle currentValue;

    ParametricStyleEditor(AnnotationStyleScheme annotationStyleScheme) {
        component = new ParametricStyleRendererComponent(annotationStyleScheme);
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                ParametricStyleEditorPane editorPane = new ParametricStyleEditorPane(currentValue);

                editorPane.addPropertyChangeListener("style", propertyEvent -> {
                    currentValue = (ParametricStyle) propertyEvent.getNewValue();
                    component.setParametricStyle(currentValue);
                });

                JDialog dialog = Dialogs.popupAutoClosingModalDialog(component, editorPane,
                                                                     new Point(0, component.getHeight()));
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        // Editor will be replaced by renderer again.
                        fireEditingStopped();
                    }
                });
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        TreePath path = ((JXTreeTable) table).getPathForRow(row);
        Annotation annotation = (Annotation) path.getLastPathComponent();

        component.setAnnotation(annotation);

        currentValue = (ParametricStyle) value;
        return component;
    }
}
