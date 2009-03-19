/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

package org.trypticon.hex.anno;

import javax.swing.tree.TreePath;

import org.trypticon.hex.swingsupport.AbstractTreeModel;

/**
 * Tree model for annotations.
 *
 * @author trejkaz
 */
public class AnnotationTreeModel extends AbstractTreeModel {
    private final AnnotationCollection annotations;

    public AnnotationTreeModel(AnnotationCollection annotations) {
        this.annotations = annotations;
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
}
