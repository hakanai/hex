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

package org.trypticon.hex.gui.notebook;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jetbrains.annotations.NotNull;

import org.trypticon.hex.AnnotationStyleScheme;
import org.trypticon.hex.HexViewer;
import org.trypticon.hex.accessory.ExpandableAccessoryBar;
import org.trypticon.hex.accessory.LocationAccessoryBar;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.gui.anno.AnnotationPane;
import org.trypticon.hex.gui.anno.CustomAnnotationStyleScheme;
import org.trypticon.hex.gui.file.SaveConfirmation;
import org.trypticon.hex.gui.file.SaveNotebookAction;
import org.trypticon.hex.gui.find.FindBar;
import org.trypticon.hex.gui.undo.GlobalUndoHelper;
import org.trypticon.hex.gui.undo.UndoHelper;
import org.trypticon.hex.gui.util.Callback;

/**
 * Pane for working with a single notebook.
 *
 * @author trejkaz
 */
public class NotebookPane extends JPanel {
    private final UndoHelper undoHelper;
    private final HexViewer viewer;
    private final JXCollapsiblePane findBarCollapser;
    private final FindBar findBar;
    private final ExpandableAccessoryBar accessoryBar;
    private final AnnotationPane annoPane;

    PropertyChangeListener nameListener = event ->
        setName((String) event.getNewValue());
    PropertyChangeListener dirtyListener = event ->
        firePropertyChange("dirty", event.getOldValue(), event.getNewValue());

    private Notebook notebook;

    /**
     * Constructs the notebook pane.
     *
     * @param notebook the notebook to view.
     */
    public NotebookPane(Notebook notebook, GlobalUndoHelper globalUndoHelper) {
        AnnotationStyleScheme annotationStyleScheme = new CustomAnnotationStyleScheme();

        undoHelper = globalUndoHelper.createUndoHelper();

        annoPane = new AnnotationPane(annotationStyleScheme, undoHelper);

        viewer = new HexViewer();
        viewer.setPreferredVisibleRowCount(36);
        viewer.setAnnotationStyleScheme(annotationStyleScheme);

        findBar = new FindBar(viewer);
        findBar.setVisible(false);

        findBarCollapser = new JXCollapsiblePane(JXCollapsiblePane.Direction.START);
        findBarCollapser.setCollapsed(true);
        findBarCollapser.setContentPane(findBar);

        accessoryBar = new ExpandableAccessoryBar(viewer);

        JPanel viewerWrapper = new JPanel(new BorderLayout());
        viewerWrapper.add(findBarCollapser, BorderLayout.PAGE_START);
        viewerWrapper.add(viewer, BorderLayout.CENTER);
        viewerWrapper.add(accessoryBar, BorderLayout.PAGE_END);

        annoPane.addPropertyChangeListener("selectedAnnotationPath", event -> {
            @SuppressWarnings("unchecked")
            List<Annotation> selectedAnnotationPath = (List<Annotation>) event.getNewValue();
            if (selectedAnnotationPath != null) {
                Annotation annotation = selectedAnnotationPath.get(selectedAnnotationPath.size() - 1);
                viewer.getSelectionModel().setCursor(annotation.getPosition());
                viewer.getSelectionModel().setCursorAndExtendSelection(annotation.getPosition() + annotation.getLength() - 1);
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(annoPane);
        splitPane.setRightComponent(viewerWrapper);
        splitPane.setDividerLocation(annoPane.getPreferredSize().width);
        splitPane.setResizeWeight(1.0); // left component gets all the extra space

        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        // Why ComponentListener doesn't work here I will never know.
        addHierarchyListener(hierarchyEvent -> viewer.requestFocusInWindow());

        setNotebook(notebook);

        // Esc closes the find bar and returns focus to the viewer.
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close-find-bar");
        getActionMap().put("close-find-bar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                hideFindBar();
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
     * Sets a new notebook to view.
     *
     * @param notebook the new notebook to view.
     */
    public void setNotebook(@NotNull Notebook notebook) {
        if (!notebook.isOpen()) {
            throw new IllegalStateException("The notebook should already be open but wasn't.");
        }

        if (this.notebook != null) {
            detachListeners();
        }

        this.notebook = notebook;

        // TODO: A proper binding API would be nice here...
        setName(notebook.getName());

        annoPane.setAnnotations(notebook.getAnnotations());
        annoPane.setBinary(notebook.getBinary());

        viewer.setAnnotations(annoPane.getExpandedAnnotations());
        viewer.setBinary(notebook.getBinary());

        attachListeners();
    }

    private void detachListeners() {
        notebook.removePropertyChangeListener("name", nameListener);
        notebook.removePropertyChangeListener("dirty", dirtyListener);
    }

    private void attachListeners() {
        notebook.addPropertyChangeListener("name", nameListener);
        notebook.addPropertyChangeListener("dirty", dirtyListener);
    }

    /**
     * Gets the find bar.
     *
     * @return the find bar.
     */
    public FindBar getFindBar() {
        return findBar;
    }

    /**
     * Shows the find bar.
     */
    public void showFindBar() {
        findBarCollapser.setCollapsed(false);
        findBar.requestFocusInWindow();
    }

    /**
     * Hides the find bar.
     */
    public void hideFindBar() {
        findBarCollapser.setCollapsed(true);
    }

    /**
     * Gets the location accessory bar.
     *
     * @return the location accessory bar.
     */
    public LocationAccessoryBar getLocationAccessoryBar() {
        return accessoryBar.getFirstAccessoryBar(LocationAccessoryBar.class);
    }

    /**
     * Gets the hex viewer.
     *
     * @return the hex viewer.
     */
    public HexViewer getViewer() {
        return viewer;
    }

    /**
     * Gets the undo manager.
     *
     * @return the undo h.
     */
    public UndoHelper getUndoHelper() {
        return undoHelper;
    }

    /**
     * <p>Tests if the notebook has been modified since the last time it was saved.</p>
     *
     * <p>This is the same as {@code getNotebook().isDirty()}, but is more convenient for tracking property changes.</p>
     *
     * @return {@code true} if the document is dirty, {@code false} otherwise.
     */
    public boolean isDirty() {
        return notebook.isDirty();
    }

    /**
     * Prepares for closing the pane.
     *
     * @param okToCloseCallback a callback which is called with {@code true} if it's okay to close or
     * {@code false} if it is not OK.
     */
    public void prepareForClose(final Callback<Boolean> okToCloseCallback) {
        // On exit, some frames might be left around in the background which have their notebooks closed already.
        if (notebook.isOpen() && notebook.isDirty()) {
            // So the user knows which one it's asking about.
            JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, this);
            if (tabbedPane != null) {
                tabbedPane.setSelectedComponent(this);
            }

            new SaveConfirmation().show(getRootPane(), option -> {
                switch (option) {
                    case CANCEL:
                        okToCloseCallback.execute(false);
                        break;
                    case DO_NOT_SAVE:
                        okToCloseCallback.execute(true);
                        break;
                    case SAVE:
                        SaveNotebookAction saveAction = (SaveNotebookAction) getRootPane().getActionMap().get("save");
                        boolean saveSucceeded = saveAction.save(getRootPane());
                        okToCloseCallback.execute(saveSucceeded);
                        break;
                    default:
                        throw new IllegalStateException("Impossible save confirmation option found");
                }
            });
        } else {
            okToCloseCallback.execute(true);
        }
    }
}
