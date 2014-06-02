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

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;

import org.trypticon.hex.anno.AbstractAnnotationCollection;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollectionEvent;
import org.trypticon.hex.anno.AnnotationCollectionListener;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.MutableAnnotation;
import org.trypticon.hex.anno.MutableAnnotationCollection;
import org.trypticon.hex.anno.MutableGroupAnnotation;
import org.trypticon.hex.anno.OverlappingAnnotationException;

/**
 * A wrapper annotation model which tracks expansion status of a tree.
 */
public class ExpansionTrackingAnnotationCollection extends AbstractAnnotationCollection
        implements MutableAnnotationCollection {

    private final JXTreeTable treeTable;
    private final MutableAnnotationCollection delegate;

    public ExpansionTrackingAnnotationCollection(JXTreeTable treeTable, MutableAnnotationCollection delegate) {
        this.treeTable = treeTable;
        this.delegate = delegate;

        treeTable.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                fireAnnotationsAdded(convertEvent(event));
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                fireAnnotationsRemoved(convertEvent(event));
            }

            private AnnotationCollectionEvent convertEvent(TreeExpansionEvent treeExpansionEvent) {
                TreePath path = treeExpansionEvent.getPath();
                Object[] pathObjects = path.getPath();

                List<GroupAnnotation> parentPath = new ArrayList<>(pathObjects.length);
                for (Object object : pathObjects) {
                    parentPath.add((GroupAnnotation) object);
                }

                List<Integer> childIndices = new ArrayList<>(pathObjects.length);
                for (int i = 0; i < pathObjects.length; i++) {
                    childIndices.add(i);
                }

                MutableGroupAnnotation groupAnnotation = (MutableGroupAnnotation) path.getLastPathComponent();
                List<? extends Annotation> children = groupAnnotation.getAnnotations();

                return new AnnotationCollectionEvent(ExpansionTrackingAnnotationCollection.this,
                                                     parentPath, childIndices, children);
            }
        });

        delegate.addAnnotationCollectionListener(new AnnotationCollectionListener() {
            @Override
            public void annotationsAdded(AnnotationCollectionEvent event) {
                if (treeTable.isExpanded(new TreePath(event.getParentPath().toArray()))) {
                    fireAnnotationsAdded(event.getParentPath(), event.getChildIndices(), event.getChildren());
                }
            }

            @Override
            public void annotationsRemoved(AnnotationCollectionEvent event) {
                if (treeTable.isExpanded(new TreePath(event.getParentPath().toArray()))) {
                    fireAnnotationsRemoved(event.getParentPath(), event.getChildIndices(), event.getChildren());
                }
            }
        });
    }

    @Override
    public GroupAnnotation getRootGroup() {
        return delegate.getRootGroup();
    }

    @Override
    public List<? extends Annotation> getTopLevel() {
        return delegate.getTopLevel();
    }

    // TODO: Methods to get the children of a node, so that it can be intercepted.

    @Override
    public List<? extends Annotation> getAnnotationPathAt(long position) {
        List<? extends Annotation> fullPath = delegate.getAnnotationPathAt(position);
        if (fullPath == null) {
            return null;
        }

        List<Annotation> path = new ArrayList<>(fullPath.size());
        TreePath treePath = new TreePath(delegate.getRootGroup());

        for (Annotation node : fullPath) {
            path.add(node);
            treePath = treePath.pathByAddingChild(node);

            if (treeTable.isCollapsed(treePath)) {
                break;
            }
        }

        return path;
    }

    @Override
    public void add(MutableAnnotation annotation) throws OverlappingAnnotationException {
        delegate.add(annotation);
    }

    @Override
    public void remove(MutableAnnotation annotation) {
        delegate.remove(annotation);
    }
}
