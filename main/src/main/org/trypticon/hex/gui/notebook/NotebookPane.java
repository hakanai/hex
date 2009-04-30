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

package org.trypticon.hex.gui.notebook;

import java.awt.BorderLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.AnnotationPane;

/**
 * Pane for working with a single notebook.
 *
 * @author trejkaz
 */
public class NotebookPane extends JPanel {
    private final Notebook notebook;
    private final HexViewer viewer;
    private final AnnotationPane annoPane;

    /**
     * Constructs the notebook pane.
     *
     * @param notebook the notebook to view.
     */
    public NotebookPane(Notebook notebook) {
        this.notebook = notebook;

        // TODO: A proper binding API would be nice here...
        setName(notebook.getName());
        notebook.addPropertyChangeListener("name", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                setName((String) event.getNewValue());
            }
        });

        viewer = new HexViewer();
        viewer.setAnnotations(notebook.getAnnotations());
        viewer.setBinary(notebook.getBinary());

        annoPane = new AnnotationPane();
        annoPane.setAnnotations(notebook.getAnnotations());

        JScrollPane viewerScroll = new JScrollPane(viewer);
        viewerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout(new BorderLayout());
        add(viewerScroll, BorderLayout.CENTER);
        add(annoPane, BorderLayout.WEST);

        // Why ComponentListener doesn't work here I will never know.
        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent hierarchyEvent) {
                viewer.requestFocusInWindow();
            }
        });
    }

    /**
     * Gets the notebook being viewed in this pane.
     *
     * @return the notebook being viewed.
     */
    public Notebook getNotebook() {
        return notebook;
    }

    /**
     * Gets the hex viewer.
     *
     * @return the hex viewer.
     */
    public HexViewer getViewer() {
        return viewer;
    }
}
