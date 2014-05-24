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

import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.Callback;

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
