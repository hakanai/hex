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

package org.trypticon.hex.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import ch.randelshofer.quaqua.JSheet;
import ch.randelshofer.quaqua.SheetEvent;
import ch.randelshofer.quaqua.SheetListener;

import org.trypticon.hex.datatransfer.DelegatingActionListener;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;

/**
 * A top-level application frame.
 *
 * XXX: It probably makes sense to replace this with OpenIDE or some other framework.
 *
 * @author trejkaz
 */
public class HexFrame extends JFrame {
    private final JTabbedPane tabbedPane;
    private final TabTitleUpdater tabTitleUpdater = new TabTitleUpdater();

    /**
     * Constructs the top-level frame.
     *
     * @param firstNotebook the first notebook to open a tab for.
     */
    public HexFrame(Notebook firstNotebook) {
        super("Hex");

        setJMenuBar(buildMenuBar(this));

        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("Quaqua.Component.visualMargin", new Insets(3,-3,-4,-3));

        // TODO: We should track if any notepads need saving and set Window.documentModified to true/false for Mac.

        addTab(firstNotebook);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        pack();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (prepareForClose()) {
                    dispose();
                }
            }
        });
    }

    /**
     * Gets the currently-selected notebook pane.
     *
     * @return the notebook pane.
     */
    public NotebookPane getNotebookPane() {
        return (NotebookPane) tabbedPane.getSelectedComponent();
    }

    /**
     * Gets all currently-open notebook panes.
     *
     * @return a list of all notebook panes.
     */
    public List<NotebookPane> getAllNotebookPanes() {
        List<NotebookPane> panes = new ArrayList<NotebookPane>(tabbedPane.getTabCount());
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            panes.add((NotebookPane) tabbedPane.getComponentAt(i));
        }
        return Collections.unmodifiableList(panes);
    }

    /**
     * Gets the notebook being viewed.
     *
     * @return the notebook being viewed.
     */
    public Notebook getNotebook() {
        return getNotebookPane().getNotebook();
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
    }

    /**
     * Closes the currently-viewed notebook.
     */
    public void closeCurrentTab() {
        NotebookPane notebookPane = (NotebookPane) tabbedPane.getSelectedComponent();
        if (notebookPane != null) {
            notebookPane.removePropertyChangeListener("name", tabTitleUpdater);

            tabbedPane.remove(notebookPane);

            notebookPane.getNotebook().close();

            // Dispose the frame if there are no tabs left.  This maintains the invariant
            // that there is always some notebook visible in the application, so we don't
            // need to worry quite so much about what to show when there isn't.
            if (tabbedPane.getTabCount() == 0) {
                dispose();
            }
        }
    }

    /**
     * Builds the application menu bar.
     *
     * @param frame the frame, or {@code null} if building the frameless menu.
     * @return the menu bar.
     */
    static JMenuBar buildMenuBar(JFrame frame) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new NewNotebookAction());
        fileMenu.add(new OpenNotebookAction());
        // TODO: Open Recent

        fileMenu.addSeparator();
        fileMenu.add(new CloseNotebookAction());
        Action saveAction = new SaveNotebookAction(false);
        if (frame != null) {
            frame.getRootPane().getActionMap().put("save", saveAction);
        }
        fileMenu.add(saveAction);
        fileMenu.add(new SaveNotebookAction(true));
        // TODO: Revert to Saved

        if (!System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            fileMenu.addSeparator();
            fileMenu.add(new ExitAction());
        }

        // TODO: Copy as:
        //  - hex
        //  - java source
        //  - ?
        JMenu editMenu = new JMenu("Edit");

        DelegatingActionListener actionListener = new DelegatingActionListener();

        JMenuItem copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.setActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME));
        copyMenuItem.addActionListener(actionListener);
        copyMenuItem.setMnemonic('c');
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(copyMenuItem);

        JMenuItem selectAllMenuItem = new JMenuItem("Select All");
        selectAllMenuItem.setActionCommand("select-all");
        selectAllMenuItem.addActionListener(actionListener);
        selectAllMenuItem.setMnemonic('a');
        selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                                                                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(selectAllMenuItem);

        editMenu.addSeparator();
        editMenu.add(new AddAnnotationMenu());

        JMenu helpMenu = new JMenu("Help");
        // TODO: Help / User Guide
        helpMenu.add(new OpenSampleNotebookAction());
        // TODO: Help / About (non-Mac only.  Mac needs to hook into the app menu.)

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    /**
     * Helper method to open a notebook in the current active frame, or a new frame
     * if there is no current frame.
     *
     * @param notebook the notebook.
     */
    public static void openNotebook(Notebook notebook) {
        HexFrame frame = findActiveFrame();

        try {
            notebook.open();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "There was a problem opening the notebook: " + e.getMessage(),
                                          "Error Opening File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (frame == null) {
            frame = new HexFrame(notebook);
            frame.setVisible(true);
        } else {
            frame.addTab(notebook);
        }
    }

    /**
     * Finds the active hex viewer.
     *
     * @return the active hex viewer.
     */
    static HexFrame findActiveFrame() {
        Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        if (window == null) {
            return null;
        }

        while (window != null && !(window instanceof HexFrame)) {
            window = window.getOwner();
        }
        return (HexFrame) window;
    }

    /**
     * Prepares for closing the frame.
     *
     * @return {@code true} if it is OK to close.
     */
    public boolean prepareForClose() {
        for (final NotebookPane pane : getAllNotebookPanes()) {
            if (pane.getNotebook().isDirty()) {
                // So the user knows which one it's asking about.
                tabbedPane.setSelectedComponent(pane);

                // XXX: It might be OK to put the name of the pane into the message, but
                // other apps don't appear to do this.
                JOptionPane optionPane = new JOptionPane(
                        "<html>" + UIManager.getString("OptionPane.css") +
                        "<b>Do you want to save changes to this document<br>" +
                        "before closing?</b><p>" +
                        "If you don't save, your changes will be lost.",
                        JOptionPane.WARNING_MESSAGE);

                Object[] options = { "Save", "Cancel", "Don't Save" };
                optionPane.setOptions(options);
                optionPane.setInitialValue(options[0]);
                optionPane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);

                class SaveSheetListener implements SheetListener {
                    private boolean okToClose = true;

                    public void optionSelected(SheetEvent event) {
                        Object value = event.getValue();
                        if (value == null || value.equals("Cancel")) {
                            okToClose = false;
                        } else if (value.equals("Don't Save")) {
                            // Nothing to do.
                        } else if (value.equals("Save")) {
                            SaveNotebookAction saveAction = (SaveNotebookAction)
                                    getRootPane().getActionMap().get("save");
                            if (!saveAction.save(HexFrame.this)) {
                                okToClose = false;
                            }
                        }
                    }
                }

                SaveSheetListener listener = new SaveSheetListener();

                JSheet.showSheet(optionPane, this, listener);

                if (!listener.okToClose) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Updates the tab title when the name of the component changes.  Should have been
     * the responsibility of {@code JTabbedPane} but Sun forgot to implement it.
     */
    private class TabTitleUpdater implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            String name = (String) event.getNewValue();
            int index = tabbedPane.indexOfComponent((Component) event.getSource());
            tabbedPane.setTitleAt(index, name);
        }
    }
}
