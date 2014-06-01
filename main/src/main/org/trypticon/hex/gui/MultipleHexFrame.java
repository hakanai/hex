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
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.anno.MutableAnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.EmptyBinary;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.Callback;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * A top-level application frame capable of opening multiple documents.
 *
 * @author trejkaz
 */
public class MultipleHexFrame extends HexFrame {
    private final JTabbedPane tabbedPane;
    private final TabTitleUpdater tabTitleUpdater = new TabTitleUpdater();
    private final TabDirtyUpdater tabDirtyUpdater = new TabDirtyUpdater();

    private NotebookPane notebookPane;

    /**
     * Constructs the top-level frame.
     *
     * @param application the application which the frame belongs to.
     */
    public MultipleHexFrame(HexApplication application) {
        super(application);
        setTitle(Resources.getString("HexFrame.title"));

        tabbedPane = new JTabbedPane();

        updateDocumentModified();

        // We add a dummy pane for size computation purposes only.
        NotebookPane dummyPane = new NotebookPane(new DummyNotebook());
        tabbedPane.addTab("", dummyPane);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        pack();

        tabbedPane.removeTabAt(0);

        tabbedPane.addChangeListener(event -> setNotebookPane((NotebookPane) tabbedPane.getSelectedComponent()));

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
    public NotebookPane getNotebookPane() {
        return notebookPane;
    }

    private void setNotebookPane(NotebookPane notebookPane) {
        NotebookPane oldNotebookPane = this.notebookPane;
        this.notebookPane = notebookPane;
        firePropertyChange("notebookPane", oldNotebookPane, notebookPane);
    }

    /**
     * Gets all currently-open notebook panes.
     *
     * @return a list of all notebook panes.
     */
    public List<NotebookPane> getAllNotebookPanes() {
        List<NotebookPane> panes = new ArrayList<>(tabbedPane.getTabCount());
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            panes.add((NotebookPane) tabbedPane.getComponentAt(i));
        }
        return Collections.unmodifiableList(panes);
    }

    /**
     * Opens a new tab for viewing a notebook.
     *
     * @param notebook the notebook to view.
     */
    public void addTab(Notebook notebook) {
        NotebookPane notebookPane = new NotebookPane(notebook);
        tabbedPane.add(notebookPane);
        tabbedPane.setSelectedComponent(notebookPane);

        notebookPane.addPropertyChangeListener("name", tabTitleUpdater);
        notebookPane.addPropertyChangeListener("dirty", tabDirtyUpdater);
        updateDocumentModified();
    }

    /**
     * Updates the "document modified" status of the window (only visible on Aqua look and feel)
     * by looking at each notebook and testing whether it is dirty.
     */
    private void updateDocumentModified() {
        boolean dirty = false;
        for (NotebookPane pane : getAllNotebookPanes()) {
            if (pane.getNotebook().isDirty()) {
                dirty = true;
                break;
            }
        }

        getRootPane().putClientProperty("Window.documentModified", dirty);
    }

    @Override
    public void closeCurrentNotebook() {
        final NotebookPane notebookPane = (NotebookPane) tabbedPane.getSelectedComponent();
        if (notebookPane != null) {
            notebookPane.prepareForClose(okToClose -> {
                if (okToClose) {
                    notebookPane.removePropertyChangeListener("name", tabTitleUpdater);
                    notebookPane.removePropertyChangeListener("dirty", tabDirtyUpdater);

                    tabbedPane.remove(notebookPane);

                    updateDocumentModified();

                    notebookPane.getNotebook().close();

                    // Dispose the frame if there are no tabs left, for Mac only, as empty windows do not exist on Mac.
                    if (PLAFUtils.isAqua()) {
                        dispose();
                    }
                }
            });
        }
    }

    @Override
    public void dispose() {
        try {
            // At the time of closing the frame, there might be tabs open still.
            for (Component tab : tabbedPane.getComponents()) {
                if (tab instanceof NotebookPane) {
                    Notebook notebook = ((NotebookPane) tab).getNotebook();
                    if (notebook.isOpen()) {
                        notebook.close();
                    }
                }
            }
        } finally {
            super.dispose();
        }
    }

    @Override
    public void prepareForClose(final Callback<Boolean> okToCloseCallback) {
        prepareForClose(getAllNotebookPanes(), okToCloseCallback);
    }

    /**
     * Prepares for closing the frame. Recursively calls itself for each pane.
     *
     * @param panes the list of panes we are yet to ask.
     * @param okToCloseCallback a callback which is called with {@code true} if all panes said it's okay
     * to close or {@code false} if one of them said it wasn't.
     */
    private void prepareForClose(List<NotebookPane> panes, final Callback<Boolean> okToCloseCallback) {
        if (panes.isEmpty()) {
            // Every pane said it was OK to close.
            okToCloseCallback.execute(true);
            return;
        }

        NotebookPane firstPane = panes.get(0);
        final List<NotebookPane> remainingPanes = panes.subList(1, panes.size());

        firstPane.prepareForClose(okToClose -> {
            if (okToClose) {
                // Reducing the risk of a StackOverflowError if there are a large number of panes open.
                SwingUtilities.invokeLater(() -> prepareForClose(remainingPanes, okToCloseCallback));
            } else {
                okToCloseCallback.execute(false);
            }
        });
    }

    /**
     * Updates the tab title when the name of the component changes.  Should have been
     * the responsibility of {@code JTabbedPane} but Sun forgot to implement it.
     */
    private class TabTitleUpdater implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String name = (String) event.getNewValue();
            int index = tabbedPane.indexOfComponent((Component) event.getSource());
            tabbedPane.setTitleAt(index, name);
        }
    }

    /**
     * Updates the dirty flag on the window itself when the dirty status of one of the notebooks changes.
     */
    private class TabDirtyUpdater implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            updateDocumentModified();
        }
    }

    /**
     * A dummy notebook class we can use for size computation.
     */
    private class DummyNotebook implements Notebook {
        private final MutableAnnotationCollection noAnnotations = new MemoryAnnotationCollection(1);

        @Override
        public void open() {
        }

        @Override
        public void close() {
        }

        @Override
        public URL getNotebookLocation() {
            return null;
        }

        @Override
        public void setNotebookLocation(URL notebookLocation) {
        }

        @Override
        public URL getBinaryLocation() {
            return null;
        }

        @Override
        public MutableAnnotationCollection getAnnotations() {
            return noAnnotations;
        }

        @Override
        public Binary getBinary() {
            return new EmptyBinary();
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public boolean isDirty() {
            return false;
        }

        @Override
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        }

        @Override
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        }
    }
}
