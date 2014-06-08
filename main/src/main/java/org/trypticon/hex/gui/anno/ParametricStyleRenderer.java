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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;

import org.trypticon.hex.AnnotationStyleScheme;
import org.trypticon.hex.anno.Annotation;

/**
 * Renders table cells for the annotation style column.
 *
 * @author trejkaz
 */
class ParametricStyleRenderer extends DefaultTableCellRenderer {
    private final ParametricStyleRendererComponent component;

    ParametricStyleRenderer(AnnotationStyleScheme annotationStyleScheme) {
        component = new ParametricStyleRendererComponent(annotationStyleScheme);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setBackground(getBackground());
        component.setForeground(getForeground());

        TreePath path = ((JXTreeTable) table).getPathForRow(row);
        Annotation annotation = (Annotation) path.getLastPathComponent();

        component.setAnnotation(annotation);

        return component;
    }

}
