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

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;

/**
 * Panel displaying the annotations.
 *
 * @author trejkaz
 */
public class AnnotationPane extends JPanel {

    private JTree annoTree;
    private AnnotationTreeModel annoTreeModel;

    public AnnotationPane() {
        annoTree = new JTree((TreeNode) null);
        setLayout(new BorderLayout());
        JScrollPane annoTreeScroll = new JScrollPane(annoTree);
        Dimension preferredSize = annoTreeScroll.getPreferredSize();
        preferredSize.width = 300;
        annoTreeScroll.setPreferredSize(preferredSize);
        add(annoTreeScroll, BorderLayout.CENTER);
    }

    public AnnotationCollection getAnnotations() {
        return annoTreeModel == null ? null : annoTreeModel.getAnnotations();
    }

    public void setAnnotations(AnnotationCollection annotations) {
        annoTreeModel = annotations == null ? null : new AnnotationTreeModel(annotations);
        annoTree.setModel(annoTreeModel);
    }
}
