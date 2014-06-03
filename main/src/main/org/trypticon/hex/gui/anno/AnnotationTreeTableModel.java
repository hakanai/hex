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

import java.util.Objects;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.AnnotationCollectionEvent;
import org.trypticon.hex.anno.AnnotationCollectionListener;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.undo.ChangeCustomStyleEdit;
import org.trypticon.hex.gui.undo.ChangeNoteEdit;
import org.trypticon.hex.gui.undo.UndoHelper;
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

    private final ExtendedAnnotationCollection annotations;
    private final Binary binary;
    private final UndoHelper undoHelper;

    public AnnotationTreeTableModel(ExtendedAnnotationCollection annotations, Binary binary, UndoHelper undoHelper) {
        this.annotations = annotations;
        this.binary = binary;
        this.undoHelper = undoHelper;

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
                if (anno instanceof ExtendedAnnotation) {
                    return ((ExtendedAnnotation) anno).getCustomStyle();
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
        ExtendedAnnotation annotation = (ExtendedAnnotation) node;
        try {
            switch (column) {
                case NOTE_COLUMN:
                    String oldNote = annotation.getNote();
                    String newNote = (String) value;
                    if (!Objects.equals(oldNote, newNote)) {
                        undoHelper.perform(new ChangeNoteEdit(annotations, annotation, oldNote, newNote));
                    }
                    break;

                case STYLE_COLUMN:
                    ParametricStyle oldCustomStyle = annotation.getCustomStyle();
                    ParametricStyle newCustomStyle = (ParametricStyle) value;
                    if (!Objects.equals(oldCustomStyle, newCustomStyle)) {
                        undoHelper.perform(
                            new ChangeCustomStyleEdit(annotations, annotation, oldCustomStyle, newCustomStyle));
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Column " + column + " is not editable");
            }
        } catch (Exception e) {
            // Should never get an exception, we expect anything in this method to be calling basic setters.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void annotationsAdded(AnnotationCollectionEvent event) {
        fireTreeNodesInserted(convertEvent(event));
    }

    @Override
    public void annotationsRemoved(AnnotationCollectionEvent event) {
        fireTreeNodesRemoved(convertEvent(event));
    }

    @Override
    public void annotationsChanged(AnnotationCollectionEvent event) {
        fireTreeNodesChanged(convertEvent(event));
    }

    private TreeModelEvent convertEvent(AnnotationCollectionEvent event) {
        Object[] path = event.getParentPath().toArray();
        int[] childIndices = event.getChildIndices().stream().mapToInt(i -> i).toArray();
        Object[] children = event.getChildren().toArray();
        return new TreeModelEvent(this, path, childIndices, children);
    }
}
