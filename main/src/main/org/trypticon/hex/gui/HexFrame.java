/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.EmptyBinary;
import org.trypticon.hex.datatransfer.DelegatingActionListener;
import org.trypticon.hex.formats.classfile.ClassFile;
import org.trypticon.hex.formats.jpeg.JpegAPP0;
import org.trypticon.hex.formats.jpeg.JpegBlock;
import org.trypticon.hex.formats.jpeg.JpegEOI;
import org.trypticon.hex.formats.jpeg.JpegSOI;
import org.trypticon.hex.formats.jpeg.JpegSOS;
import org.trypticon.hex.gui.formats.DropStructureAction;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.prefs.WorkspaceStateTracker;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

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
     */
    public HexFrame() {
        super("Hex");

        setJMenuBar(buildMenuBar(this));

        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("Quaqua.Component.visualMargin", new Insets(3,-3,-4,-3));

        // TODO: We should track if any notepads need saving and set Window.documentModified to true/false for Mac.

        // We add a dummy pane for size computation purposes only.
        NotebookPane dummyPane = new NotebookPane(new DummyNotebook());
        tabbedPane.addTab("", dummyPane);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        pack();

        tabbedPane.removeTabAt(0);

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
        return getNotebookPane() == null ? null : getNotebookPane().getNotebook();
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
            if (notebookPane.prepareForClose()) {
                notebookPane.removePropertyChangeListener("name", tabTitleUpdater);

                tabbedPane.remove(notebookPane);

                notebookPane.getNotebook().close();

                // Dispose the frame if there are no tabs left, for Mac only, as empty windows do not exist on Mac.
                if (PLAFUtils.isQuaqua()) {
                    dispose();
                }
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
        PreferredDirectoryManager preferredDirectoryManager = new PreferredDirectoryManager();

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new NewNotebookAction(preferredDirectoryManager));
        fileMenu.add(new OpenNotebookAction(preferredDirectoryManager));
        // TODO: Open Recent

        fileMenu.addSeparator();
        fileMenu.add(new CloseNotebookAction());
        Action saveAction = new SaveNotebookAction(preferredDirectoryManager, false);
        if (frame != null) {
            frame.getRootPane().getActionMap().put("save", saveAction);
        }
        fileMenu.add(saveAction);
        fileMenu.add(new SaveNotebookAction(preferredDirectoryManager, true));
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
        editMenu.add(new RemoveAnnotationAction());

        editMenu.addSeparator();
        editMenu.add(new AddSubRegionAction());
        editMenu.add(new RemoveSubRegionAction());

        editMenu.addSeparator();
        editMenu.add(buildFormatsMenu());

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

    private static JMenu buildFormatsMenu() {
        JMenu menu = new JMenu("Formats");
        menu.add(new DropStructureAction(new ClassFile()));
        menu.addSeparator();
        menu.add(new DropStructureAction(new JpegSOI()));
        menu.add(new DropStructureAction(new JpegAPP0()));
        menu.add(new DropStructureAction(new JpegBlock()));
        menu.add(new DropStructureAction(new JpegSOS()));
        menu.add(new DropStructureAction(new JpegEOI()));
        return menu;
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

        ensureFrameOpen().addTab(notebook);
    }

    /**
     * Ensures that the frame is open.
     *
     * @return a reference to the frame.
     */
    public static HexFrame ensureFrameOpen() {
        HexFrame frame = findActiveFrame();
        if (frame == null) {
            frame = new HexFrame();
            frame.setVisible(true);
        }
        return frame;
    }

    /**
     * Finds the active hex viewer.
     *
     * @return the active hex viewer.
     */
    public static HexFrame findActiveFrame() {
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
        new WorkspaceStateTracker().save();

        for (final NotebookPane pane : getAllNotebookPanes()) {
            if (!pane.prepareForClose()) {
                return false;
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

    /**
     * A dummy notebook class we can use for size computation.
     */
    private class DummyNotebook implements Notebook {
        private final AnnotationCollection noAnnotations = new MemoryAnnotationCollection(0);
        public void open() {}
        public void close() {}
        public URL getNotebookLocation() { return null; }
        public void setNotebookLocation(URL notebookLocation) {}
        public URL getBinaryLocation() { return null; }
        public AnnotationCollection getAnnotations() { return noAnnotations; }
        public Binary getBinary() { return new EmptyBinary(); }
        public boolean isOpen() { return false; }
        public String getName() { return ""; }
        public boolean isDirty() { return false; }
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {}
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {}
    }
}
