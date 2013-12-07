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
import java.awt.Frame;
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
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultFocusManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.EmptyBinary;
import org.trypticon.hex.datatransfer.DelegatingActionListener;
import org.trypticon.hex.formats.ruby.RubyStructureDSL;
import org.trypticon.hex.gui.formats.DropStructureAction;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.prefs.WorkspaceStateTracker;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.gui.util.Callback;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * A top-level application frame.
 * <p/>
 * XXX: It probably makes sense to replace this with OpenIDE or some other framework.
 *
 * @author trejkaz
 */
public abstract class HexFrame extends JFrame {
    private final HexApplication application;

    /**
     * Constructs the frame.
     *
     * @param application the application which the frame belongs to.
     */
    protected HexFrame(HexApplication application) {
        this.application = application;

        setJMenuBar(new MenuBarBuilder(application).buildMenuBar(this));
    }

    /**
     * Gets the application which the frame belongs to.
     *
     * @return the application.
     */
    protected HexApplication getApplication() {
        return application;
    }

    /**
     * Gets the currently-selected notebook pane.
     *
     * @return the notebook pane.
     */
    public abstract NotebookPane getNotebookPane();

    /**
     * Gets the notebook being viewed. Just a convenience method for {@code getNotebookPane().getNotebook()}.
     *
     * @return the notebook being viewed.
     */
    public Notebook getNotebook() {
        return getNotebookPane() == null ? null : getNotebookPane().getNotebook();
    }

    /**
     * Closes the currently-viewed notebook.
     */
    public abstract void closeCurrentNotebook();

    /**
     * Prepares for closing the frame.
     *
     * @param okToCloseCallback a callback which is called with {@code true} if it's okay to close or
     * {@code false} if it is not OK to close.
     */
    public abstract void prepareForClose(final Callback<Boolean> okToCloseCallback);

    /**
     * Finds all open hex frames.
     *
     * @return all frames.
     */
    public static List<HexFrame> findAllFrames() {
        final List<HexFrame> frames = new LinkedList<>();
        for (Frame frame : Frame.getFrames()) {
            if (frame instanceof HexFrame && frame.isDisplayable()) {
                frames.add((HexFrame) frame);
            }
        }
        return frames;
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

}
