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

package org.trypticon.hex.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.Callback;

/**
 * A top-level application frame capable of opening a single document.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class SingleHexFrame extends HexFrame {
    private final NotebookPane notebookPane;

    /**
     * Constructs the top-level frame.
     *
     * @param application the application which the frame belongs to.
     */
    public SingleHexFrame(HexApplication application, Notebook notebook) {
        super(application);

        notebookPane = new NotebookPane(notebook, application.getGlobalUndoHelper());
        notebookPane.addPropertyChangeListener("unsaved", event -> updateDocumentModified());

        updateTitle();
        notebookPane.getNotebook().addPropertyChangeListener("notebookLocation", event -> updateTitle());

        setLayout(new BorderLayout());
        add(notebookPane, BorderLayout.CENTER);
        pack();

        updateDocumentModified();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                prepareForClose(okToClose -> {
                    if (okToClose) {
                        dispose();
                    }
                });
            }
        });
    }

    @Override
    @Nullable
    public NotebookPane getNotebookPane() {
        return notebookPane;
    }

    @Override
    public List<NotebookPane> getAllNotebookPanes() {
        return Arrays.asList(notebookPane);
    }

    @Override
    public void bringToFront(NotebookPane notebookPane) {
        requestFocus();
    }

    @Override
    public void closeCurrentNotebook() {
        notebookPane.prepareForClose(okToClose -> {
            if (okToClose) {
                dispose();
            }
        });
    }

    private void updateTitle() {
        setTitle(notebookPane.getNotebookName());
        getRootPane().putClientProperty("Window.documentFile", notebookPane.getNotebookFile());
    }

    /**
     * Updates the "document modified" status of the window (only visible on Aqua look and feel)
     * by looking at each notebook and testing whether it is dirty.
     */
    private void updateDocumentModified() {
        getRootPane().putClientProperty("Window.documentModified", notebookPane.isUnsaved());
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
}
