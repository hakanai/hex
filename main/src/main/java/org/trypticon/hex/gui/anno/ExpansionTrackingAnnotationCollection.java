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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jetbrains.annotations.Contract;

import org.trypticon.hex.anno.AbstractAnnotationCollection;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.AnnotationCollectionEvent;
import org.trypticon.hex.anno.AnnotationCollectionListener;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.OverlappingAnnotationException;

/**
 * A wrapper annotation model which tracks expansion status of a tree.
 */
public class ExpansionTrackingAnnotationCollection extends AbstractAnnotationCollection
        implements AnnotationCollection {

    private final JXTreeTable treeTable;
    private final AnnotationCollection delegate;

    public ExpansionTrackingAnnotationCollection(JXTreeTable treeTable, AnnotationCollection delegate) {
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

                GroupAnnotation groupAnnotation = (GroupAnnotation) path.getLastPathComponent();
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

            @Override
            public void annotationsChanged(AnnotationCollectionEvent event) {
                if (treeTable.isExpanded(new TreePath(event.getParentPath().toArray()))) {
                    fireAnnotationsChanged(event.getParentPath(), event.getChildIndices(), event.getChildren());
                }
            }
        });
    }

    @Nonnull
    @Override
    public GroupAnnotation getRootGroup() {
        return delegate.getRootGroup();
    }

    @Nonnull
    @Override
    public List<? extends Annotation> getTopLevel() {
        return delegate.getTopLevel();
    }

    // TODO: Methods to get the children of a node, so that it can be intercepted.

    @Nullable
    @Override
    public List<? extends Annotation> getAnnotationPathAt(long position) {
        return getExpandedAnnotationPath(delegate.getAnnotationPathAt(position));
    }

    @Nonnull
    @Override
    public List<? extends Annotation> getAnnotationPathFor(Annotation annotation) {
        return getExpandedAnnotationPath(delegate.getAnnotationPathFor(annotation));
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    private List<? extends Annotation> getExpandedAnnotationPath(@Nullable List<? extends Annotation> fullPath) {
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
    public void add(@Nonnull Annotation annotation) throws OverlappingAnnotationException {
        delegate.add(annotation);
    }

    @Override
    public void remove(@Nonnull Annotation annotation) {
        delegate.remove(annotation);
    }
}
