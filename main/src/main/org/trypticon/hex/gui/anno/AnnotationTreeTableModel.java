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

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.AnnotationCollectionEvent;
import org.trypticon.hex.anno.AnnotationCollectionListener;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.MutableAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.AbstractTreeTableModel;

/**
 * Tree model for annotations.
 *
 * @author trejkaz
 */
public class AnnotationTreeTableModel extends AbstractTreeTableModel
    implements AnnotationCollectionListener {

    static final int TYPE_COLUMN = 0;
    static final int VALUE_COLUMN = 1;
    static final int NOTE_COLUMN = 2;
    static final int STYLE_COLUMN = 3;
    private static final int COLUMN_COUNT = 4;

    private static final String[] columnNames = {
        Resources.getString("AnnotationViewer.Columns.type"),
        Resources.getString("AnnotationViewer.Columns.value"),
        Resources.getString("AnnotationViewer.Columns.notes"),
        Resources.getString("AnnotationViewer.Columns.style")
    };

    private final AnnotationCollection annotations;
    private final Binary binary;

    public AnnotationTreeTableModel(AnnotationCollection annotations, Binary binary) {
        this.annotations = annotations;
        this.binary = binary;

        annotations.addAnnotationCollectionListener(this);
    }

    public AnnotationCollection getAnnotations() {
        return annotations;
    }

    @Override
    public Object getRoot() {
        return annotations.getRootGroup();
    }

    @Override
    public boolean isLeaf(Object o) {
        return !(o instanceof GroupAnnotation);
    }

    @Override
    public int getChildCount(Object node) {
        if (node instanceof GroupAnnotation) {
            return ((GroupAnnotation) node).getAnnotations().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getChild(Object node, int index) {
        if (node instanceof GroupAnnotation) {
            return ((GroupAnnotation) node).getAnnotations().get(index);
        } else {
            return null;
        }
    }

    @Override
    public int getIndexOfChild(Object node, Object child) {
        if (node instanceof GroupAnnotation) {
            //noinspection SuspiciousMethodCalls
            return ((GroupAnnotation) node).getAnnotations().indexOf(child);
        } else {
            return -1;
        }
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) {
        // I don't think I will need this.
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public int getHierarchicalColumn() {
        return TYPE_COLUMN;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case TYPE_COLUMN:
            case VALUE_COLUMN:
                return Object.class;
            case NOTE_COLUMN:
                return String.class;
            case STYLE_COLUMN:
                return ParametricStyle.class;
            default:
                throw new IllegalArgumentException("Column " + column + " is out of bounds");
        }
    }

    @Override
    public Object getValueAt(Object node, int column) {
        Annotation anno = (Annotation) node;
        switch (column) {
            case TYPE_COLUMN:
                if (anno instanceof GroupAnnotation) {
                    // TODO: It would be nice if groups could have the name of what they represent.
                    return Resources.getString("AnnotationViewer.Cells.group");
                } else {
                    return ((Annotation) node).getInterpreter();
                }
            case VALUE_COLUMN:
                if (anno instanceof GroupAnnotation) {
                    // XXX: Later we do want to support groups with interpretations.
                    return null;
                } else {
                    return anno.interpret(binary);
                }
            case NOTE_COLUMN:
                return anno.getNote();
            case STYLE_COLUMN:
                if (anno instanceof AnnotationExtensions) {
                    return ((AnnotationExtensions) anno).getCustomStyle();
                } else {
                    return null;
                }
            default:
                throw new IllegalArgumentException("Column " + column + " is out of bounds");
        }
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        switch (column) {
            case NOTE_COLUMN:
            case STYLE_COLUMN:
                return true;

            default:
                return false;
        }
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
        switch (column)
        {
            case NOTE_COLUMN:
                ((MutableAnnotation) node).setNote((String) value);
                // TODO: Set a dirty flag somewhere.
                // TODO: Undo
                break;

            case STYLE_COLUMN:
                if (node instanceof AnnotationExtensions) {
                    ((AnnotationExtensions) node).setCustomStyle((ParametricStyle) value);
                }
                break;

            default:
                throw new IllegalArgumentException("Column " + column + " is not editable");
        }
    }

    @Override
    public void annotationsChanged(AnnotationCollectionEvent event) {
        // TODO: This will change a bit once we have some more structure.
        fireTreeStructureChanged(new TreeModelEvent(this, new Object[] { getRoot() }));
    }
}
