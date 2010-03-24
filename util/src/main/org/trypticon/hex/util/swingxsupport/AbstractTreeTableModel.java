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

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

/**
 * Base class for implementing custom tree models, since Sun forgot to implement one.
 *
 * @author trejkaz
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {
    private EventListenerList listenerList;

    protected void fireTreeNodesInserted(TreeModelEvent event) {
        if (listenerList != null) {
            for (TreeModelListener listener : listenerList.getListeners(TreeModelListener.class)) {
                listener.treeNodesInserted(event);
            }
        }
    }

    protected void fireTreeNodesRemoved(TreeModelEvent event) {
        if (listenerList != null) {
            for (TreeModelListener listener : listenerList.getListeners(TreeModelListener.class)) {
                listener.treeNodesRemoved(event);
            }
        }
    }

    protected void fireTreeNodesChanged(TreeModelEvent event) {
        if (listenerList != null) {
            for (TreeModelListener listener : listenerList.getListeners(TreeModelListener.class)) {
                listener.treeNodesChanged(event);
            }
        }
    }

    protected void fireTreeStructureChanged(TreeModelEvent event) {
        if (listenerList != null) {
            for (TreeModelListener listener : listenerList.getListeners(TreeModelListener.class)) {
                listener.treeStructureChanged(event);
            }
        }
    }

    public void addTreeModelListener(TreeModelListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(TreeModelListener.class, listener);
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        if (listenerList != null) {
            listenerList.remove(TreeModelListener.class, listener);
        }
    }
}
