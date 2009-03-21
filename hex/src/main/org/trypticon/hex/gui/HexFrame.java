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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;

import org.trypticon.hex.datatransfer.DelegatingActionListener;
import org.trypticon.hex.anno.AnnotationPane;
import org.trypticon.hex.HexViewer;
import org.trypticon.hex.gui.notebook.Notebook;

/**
 * A top-level application frame.
 *
 * XXX: It probably makes sense to replace this with OpenIDE or some other framework.
 *
 * @author trejkaz
 */
public class HexFrame extends JFrame {
    private final HexViewer viewer;
    private final AnnotationPane annoPane;
    private Notebook notebook;

    /**
     * Constructs the top-level frame.
     */
    public HexFrame() {
        super("Hex");

        setJMenuBar(buildMenuBar());

        viewer = new HexViewer();
        annoPane = new AnnotationPane();


        JScrollPane viewerScroll = new JScrollPane(viewer);
        viewerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(viewerScroll, BorderLayout.CENTER);
        getContentPane().add(annoPane, BorderLayout.WEST);
        pack();
    }

    /**
     * Gets the notebook being viewed.
     *
     * @return the notebook being viewed.
     */
    public Notebook getNotebook() {
        return notebook;
    }

    /**
     * Sets the notebook to view.
     *
     * @param notebook the notebook to view.
     * @throws IOException if there was an error opening the notebook.
     */
    public void setNotebook(Notebook notebook) throws IOException {
        if (this.notebook != null) {
            this.notebook.close();

            annoPane.setAnnotations(null);
            viewer.setAnnotations(null);
            viewer.setBinary(null);
        }

        this.notebook = notebook;

        if (notebook != null) {
            annoPane.setAnnotations(notebook.getAnnotations());
            viewer.setAnnotations(notebook.getAnnotations());

            notebook.open();

            viewer.setBinary(notebook.getBinary());
        }
    }


    private JMenuBar buildMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new NewNotebookAction());
        fileMenu.add(new OpenNotebookAction());
        // TODO: Open Recent

        fileMenu.addSeparator();
        // TODO: Close Notebook - not useful for me until we maintain state.
        fileMenu.add(new SaveNotebookAction(false));
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

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }

    /**
     * Sets initial focus, which is to move the focus to the viewer.
     */
    public void initialFocus() {
        viewer.requestFocusInWindow();
    }

    /**
     * Action to exit the application.
     */
    private class ExitAction extends AbstractAction {
        private ExitAction() {
            putValue(NAME, "Exit");
            putValue(MNEMONIC_KEY, (int) 'x');
        }

        public void actionPerformed(ActionEvent event) {
            dispose();
        }
    }
}
