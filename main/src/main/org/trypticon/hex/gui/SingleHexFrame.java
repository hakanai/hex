/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2012  Trejkaz, Hex Project
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

package org.trypticon.hex.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.prefs.WorkspaceStateTracker;
import org.trypticon.hex.gui.util.Callback;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * A top-level application frame capable of opening a single document.
 *
 * @author trejkaz
 */
public class SingleHexFrame extends HexFrame {
    private final NotebookPane notebookPane;

    /**
     * Constructs the top-level frame.
     */
    public SingleHexFrame(Notebook notebook) {
        setTitle(notebook.getName());

        setJMenuBar(new MenuBarBuilder().buildMenuBar(this));

        notebookPane = new NotebookPane(notebook);
        notebookPane.addPropertyChangeListener("name", new TitleUpdater());
        notebookPane.removePropertyChangeListener("dirty", new DirtyUpdater());

        setLayout(new BorderLayout());
        add(notebookPane, BorderLayout.CENTER);
        pack();

        updateDocumentModified();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                prepareForClose(new Callback<Boolean>() {
                    @Override
                    public void execute(Boolean okToClose) {
                        if (okToClose) {
                            dispose();
                        }
                    }
                });
            }
        });
    }

    @Override
    public NotebookPane getNotebookPane() {
        return notebookPane;
    }

    @Override
    public void closeCurrentNotebook() {
        notebookPane.prepareForClose(new Callback<Boolean>() {
            @Override
            public void execute(Boolean okToClose) {
                if (okToClose) {
                    dispose();
                }
            }
        });
    }

    /**
     * Updates the "document modified" status of the window (only visible on Aqua look and feel)
     * by looking at each notebook and testing whether it is dirty.
     */
    private void updateDocumentModified() {
        getRootPane().putClientProperty("Window.documentModified", notebookPane.getNotebook().isDirty());
    }

    @Override
    public void dispose() {
        try {
            Notebook notebook = notebookPane.getNotebook();
            if (notebook.isOpen()) {
                notebook.close();
            }
        } finally {
            super.dispose();
        }
    }

    @Override
    public void prepareForClose(final Callback<Boolean> okToCloseCallback) {
        notebookPane.prepareForClose(okToCloseCallback);
    }

    /**
     * Updates the tab title when the name of the component changes.  Should have been
     * the responsibility of {@code JTabbedPane} but Sun forgot to implement it.
     */
    private class TitleUpdater implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String name = (String) event.getNewValue();
            setTitle(name);
        }
    }

    /**
     * Updates the dirty flag on the window itself when the dirty status of one of the notebooks changes.
     */
    private class DirtyUpdater implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            updateDocumentModified();
        }
    }
}
