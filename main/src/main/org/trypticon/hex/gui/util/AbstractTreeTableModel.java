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

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * Base class for implementing custom tree models, adding some convenience methods onto the basic one
 * provided by SwingX.
 *
 * @author trejkaz
 */
public abstract class AbstractTreeTableModel extends org.jdesktop.swingx.treetable.AbstractTreeTableModel
        implements TreeTableModel {

    public AbstractTreeTableModel() {
        super();
    }

    public AbstractTreeTableModel(Object root) {
        super(root);
    }

    protected void fireTreeNodesInserted(TreeModelEvent event) {
        for (TreeModelListener listener : getTreeModelListeners()) {
            listener.treeNodesInserted(event);
        }
    }

    protected void fireTreeNodesRemoved(TreeModelEvent event) {
        for (TreeModelListener listener : getTreeModelListeners()) {
            listener.treeNodesRemoved(event);
        }
    }

    protected void fireTreeNodesChanged(TreeModelEvent event) {
        for (TreeModelListener listener : getTreeModelListeners()) {
            listener.treeNodesChanged(event);
        }
    }

    protected void fireTreeStructureChanged(TreeModelEvent event) {
        for (TreeModelListener listener : getTreeModelListeners()) {
            listener.treeStructureChanged(event);
        }
    }
}
