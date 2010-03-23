/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

package org.trypticon.hex.util.swingxsupport;

import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * An empty model for use with {@link org.jdesktop.swingx.JXTreeTable JXTreeTable}.  {@code JXTreeTable} does not permit setting the model
 * to {@code null} like a lot of Swing components.
 */
public class NullTreeTableModel implements TreeTableModel {
    public Class<?> getColumnClass(int i) {
        return Object.class;
    }

    public int getColumnCount() {
        return 0;
    }

    public String getColumnName(int i) {
        return null;
    }

    public int getHierarchicalColumn() {
        return 0;
    }

    public Object getValueAt(Object o, int i) {
        return null;
    }

    public boolean isCellEditable(Object o, int i) {
        return false;
    }

    public void setValueAt(Object o, Object o1, int i) {
    }

    public Object getRoot() {
        return null;
    }

    public Object getChild(Object parent, int index) {
        return null;
    }

    public int getChildCount(Object parent) {
        return 0;
    }

    public boolean isLeaf(Object node) {
        return true;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
    }

    public void removeTreeModelListener(TreeModelListener l) {
    }
}
