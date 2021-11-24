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

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import org.trypticon.hex.AnnotationStyleScheme;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.gui.undo.UndoHelper;
import org.trypticon.hex.gui.util.FilteredActionMap;
import org.trypticon.hex.gui.util.NullTreeTableModel;

/**
 * Panel displaying the annotations.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class AnnotationPane extends JPanel {

    private final UndoHelper undoHelper;

    private JXTreeTable annoTreeTable;
    private TreeTableModel annoTreeTableModel = new NullTreeTableModel();

    @Nullable
    private ExtendedAnnotationCollection annotations;

    @Nullable
    private Binary binary;

    @Nullable
    private List<Annotation> selectedAnnotationPath;

    public AnnotationPane(AnnotationStyleScheme annotationStyleScheme, UndoHelper undoHelper) {
        annoTreeTable = new AnnotationTreeTable(annotationStyleScheme);
        this.undoHelper = undoHelper;

        annoTreeTable.setActionMap(new FilteredActionMap(annoTreeTable.getActionMap(), "find"));

        annoTreeTable.addTreeSelectionListener(treeSelectionEvent -> {
            TreePath treePath = annoTreeTable.getTreeSelectionModel().getSelectionPath();
            if (treePath == null) {
                setSelectedAnnotationPath(null);
            } else {
                Object[] treePathArray = treePath.getPath();
                Annotation[] pathArray = new Annotation[treePathArray.length - 1];
                // We know every element will really be Annotation.
                //noinspection SuspiciousSystemArraycopy
                System.arraycopy(treePathArray, 1, pathArray, 0, pathArray.length);
                setSelectedAnnotationPath(Arrays.asList(pathArray));
            }
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(annoTreeTable), BorderLayout.CENTER);
    }

    @Nullable
    public AnnotationCollection getAnnotations() {
        if (annoTreeTableModel instanceof AnnotationTreeTableModel) {
            return ((AnnotationTreeTableModel) annoTreeTableModel).getAnnotations();
        } else {
            return null; // happens when it's the null model.
        }
    }

    public void setAnnotations(ExtendedAnnotationCollection annotations) {
        this.annotations = annotations;
        createModelIfComplete();
    }

    @Nullable
    public List<Annotation> getSelectedAnnotationPath() {
        return selectedAnnotationPath;
    }

    private void setSelectedAnnotationPath(@Nullable List<Annotation> selectedAnnotationPath) {
        List<Annotation> oldSelectedAnnotationPath = this.selectedAnnotationPath;
        this.selectedAnnotationPath = selectedAnnotationPath == null ? null : Collections.unmodifiableList(selectedAnnotationPath);
        firePropertyChange("selectedAnnotationPath", oldSelectedAnnotationPath, selectedAnnotationPath);
    }

    public void setBinary(@Nullable Binary binary) {
        this.binary = binary;
        createModelIfComplete();
    }

    private void createModelIfComplete() {
        if (annotations != null && binary != null) {
            annoTreeTableModel = new AnnotationTreeTableModel(annotations, binary, undoHelper);
        } else {
            annoTreeTableModel = new NullTreeTableModel();
        }
        annoTreeTable.setTreeTableModel(annoTreeTableModel);
    }

    /**
     * Gets an annotation collection which hides the nodes which are not expanded in the tree.
     *
     * @return the annotation collection.
     */
    public AnnotationCollection getExpandedAnnotations() {
        if (annotations == null) {
            throw new IllegalStateException("Expected a call to setAnnotations " +
                                            "before calling getExpandedAnnotations!");
        }
        return new ExpansionTrackingAnnotationCollection(annoTreeTable, annotations);
    }
}
