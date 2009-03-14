/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.TransferHandler;

import org.trypticon.binary.Binary;
import org.trypticon.binary.BinaryFactory;
import org.trypticon.hex.datatransfer.DelegatingActionListener;

/**
 * A top-level application frame.
 *
 * XXX: It probably makes sense to replace this with OpenIDE or some other framework.
 *
 * @author trejkaz
 */
public class HexFrame extends JFrame {
    private final HexViewer viewer;

    /**
     * Constructs the top-level frame.
     */
    public HexFrame() {
        super("Hex");

        setJMenuBar(buildMenuBar());

        viewer = new HexViewer();

        JScrollPane viewerScroll = new JScrollPane(viewer);
        viewerScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(viewerScroll, BorderLayout.CENTER);
        pack();
    }

    /**
     * Loads a file into the viewer within this frame.
     *
     * @param file the file to load.
     */
    public void loadFile(File file) {
        try {
            Binary binary = BinaryFactory.open(file);
            viewer.setBinary(binary);
            pack();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "There was an error opening the file.");
        }
    }

    private JMenuBar buildMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new OpenAction());

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

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }

    /**
     * Action to open a new file for viewing.
     */
    private class OpenAction extends AbstractAction {
        private OpenAction() {
            putValue(NAME, "Open...");
            putValue(MNEMONIC_KEY, (int) 'o');
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,
                                                             Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        public void actionPerformed(ActionEvent event) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(getRootPane()) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                loadFile(file);
            }
        }
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
