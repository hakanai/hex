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

import javax.annotation.Nullable;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * An empty model for use with {@link org.jdesktop.swingx.JXTreeTable JXTreeTable}.  {@code JXTreeTable} does not permit setting the model
 * to {@code null} like a lot of Swing components.
 *
 * @author trejkaz
 */
public class NullTreeTableModel implements TreeTableModel {
    @Override
    public Class<?> getColumnClass(int i) {
        return Object.class;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    @Nullable
    public String getColumnName(int i) {
        return null;
    }

    @Override
    public int getHierarchicalColumn() {
        return 0;
    }

    @Override
    @Nullable
    public Object getValueAt(Object o, int i) {
        return null;
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return false;
    }

    @Override
    public void setValueAt(Object o, Object o1, int i) {
    }

    @Override
    @Nullable
    public Object getRoot() {
        return null;
    }

    @Override
    @Nullable
    public Object getChild(Object parent, int index) {
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return true;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }
}
