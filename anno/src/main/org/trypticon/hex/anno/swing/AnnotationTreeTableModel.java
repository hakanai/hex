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

package org.trypticon.hex.anno.swing;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.AnnotationCollectionEvent;
import org.trypticon.hex.anno.AnnotationCollectionListener;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.util.swingxsupport.AbstractTreeTableModel;

/**
 * Tree model for annotations.
 *
 * @author trejkaz
 */
public class AnnotationTreeTableModel extends AbstractTreeTableModel implements AnnotationCollectionListener {
    static final int TYPE_COLUMN = 0;
    static final int VALUE_COLUMN = 1;
    static final int NOTE_COLUMN = 2;
    private static final int COLUMN_COUNT = 3;

    private static final String[] COLUMN_NAMES = { "Type", "Value", "Notes" }; // TODO: Localise

    private final AnnotationCollection annotations;
    private final Binary binary;

    public AnnotationTreeTableModel(AnnotationCollection annotations, Binary binary) {
        this.annotations = annotations;
        this.binary = binary;

        annotations.addAnnotationCollectionListener(this);
    }

    private Object rootNode = new Object() {
        @Override
        public String toString() {
            return "Annotations";
        }
    };

    public AnnotationCollection getAnnotations() {
        return annotations;
    }

    public Object getRoot() {
        return rootNode;
    }

    public boolean isLeaf(Object o) {
        return o != rootNode;
    }

    public int getChildCount(Object o) {
        if (o == rootNode) {
            return annotations.getAll().size();
        } else {
            return 0;
        }
    }

    public Object getChild(Object o, int i) {
        if (o == rootNode) {
            return annotations.getAll().get(i);
        } else {
            return null;
        }
    }

    public int getIndexOfChild(Object o, Object child) {
        if (o == rootNode) {
            //noinspection SuspiciousMethodCalls
            return annotations.getAll().indexOf(child);
        } else {
            return -1;
        }
    }

    public void valueForPathChanged(TreePath treePath, Object o) {
        // I don't think I will need this.
    }

    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    public int getHierarchicalColumn() {
        return TYPE_COLUMN;
    }

    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case TYPE_COLUMN:
            case VALUE_COLUMN:
                return Object.class;
            case NOTE_COLUMN:
                return String.class;
            default:
                throw new IllegalArgumentException("Column " + column + " is out of bounds");
        }
    }

    public Object getValueAt(Object node, int column) {
        if (node == rootNode) {
            return null;
        }

        Annotation anno = (Annotation) node;
        switch (column) {
            case TYPE_COLUMN:
                return ((Annotation) node).getInterpretor();
            case VALUE_COLUMN:
                return anno.interpret(binary);
            case NOTE_COLUMN:
                return anno.getNote();
            default:
                throw new IllegalArgumentException("Column " + column + " is out of bounds");
        }
    }

    public boolean isCellEditable(Object o, int i) {
        // TODO: Make Notes at least directly editable.
        return false;
    }

    public void setValueAt(Object o, Object o1, int i) {
        throw new UnsupportedOperationException("Editing not supported right now");
    }

    public void annotationsChanged(AnnotationCollectionEvent event) {
        // TODO: This will change a bit once we have some more structure.
        fireTreeStructureChanged(new TreeModelEvent(this, new Object[] { rootNode }));
    }
}
