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

import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import javax.swing.DefaultFocusManager;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.gui.prefs.WorkspaceStateTracker;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.gui.util.Callback;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Manages the top-level application logic.
 *
 * @author trejkaz
 */
public class HexApplication {
    /**
     * Constructs the application.
     */
    public HexApplication() {
        new PLAFBootstrap().init(this);
    }

    /**
     * Opens windows which should be open on startup.
     */
    public void openInitialWindows() {
        // If not running on Aqua, it is impossible to start up without at least one document open.
        // For now, we will resolve this by opening the sample but another way would be supporting the
        // frame having no documents open (which would be bad on Mac...)
        if (!PLAFUtils.isAqua()) {
            JFrame frame = new MultipleHexFrame(this);
            frame.setVisible(true);
        }

        final WorkspaceStateTracker stateTracker = WorkspaceStateTracker.create(this);
        boolean restoredState = stateTracker.restore();

        // Open a sample notebook if this is the first time the application was ever opened.
        if (!restoredState) {
            new OpenSampleNotebookAction(this).actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Open Sample Notebook"));
        }
    }

    /**
     * Opens a notebook from a file path.
     *
     * @param notebookPath the notebook path.
     * @return the frame the notebook was opened in, or {@code null} if there was a problem opening it
     *         (in this situation the user would have been alerted already.)
     */
    public HexFrame openNotebook(Path notebookPath) {
        try {
            Notebook notebook = new NotebookStorage().read(notebookPath.toUri().toURL());
            return openNotebook(notebook);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("The JRE created a URL which was malformed: " + notebookPath, e);
        } catch (IOException e) {
            Window activeWindow = DefaultFocusManager.getCurrentManager().getActiveWindow();
            JOptionPane.showMessageDialog(activeWindow, "There was a problem opening the notebook: " + e.getMessage(),
                                          "Error Opening File", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * <p>Opens a notebook.</p>
     *
     * <p>May open a new frame or may use an existing frame. This depends on what platform you're running on.</p>
     *
     * @param notebook the notebook.
     * @return the frame the notebook was opened in, or {@code null} if there was a problem opening it
     *         (in this situation the user would have been alerted already.)
     */
    public HexFrame openNotebook(Notebook notebook) {
        return openNotebook(notebook, true);
    }

    /**
     * <p>Opens a notebook.</p>
     *
     * <p>May open a new frame or may use an existing frame. This depends on what platform you're running on.</p>
     *
     * @param notebook the notebook.
     * @param openFrameImmediately if {@code true}, the frame will be opened immediately. If {@code false},
     *        it will not be opened, allowing the caller to possibly change it before making it visible.
     * @return the frame the notebook was opened in, or {@code null} if there was a problem opening it
     *         (in this situation the user would have been alerted already.)
     */
    public HexFrame openNotebook(Notebook notebook, boolean openFrameImmediately) {
        Window activeWindow = DefaultFocusManager.getCurrentManager().getActiveWindow();

        try {
            notebook.open();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(activeWindow, "There was a problem opening the notebook: " + e.getMessage(),
                                          "Error Opening File", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (PLAFUtils.isAqua()) {
            // Try to mimic document-based Mac applications better by using a separate frame per notebook.
            SingleHexFrame frame = new SingleHexFrame(this, notebook);
            if (openFrameImmediately) {
                frame.setVisible(true);
            }
            return frame;
        } else {
            MultipleHexFrame frame = (MultipleHexFrame) HexFrame.findActiveFrame();
            frame.addTab(notebook);
            return frame;
        }
    }

    /**
     * Tries to exit the application.
     *
     * @param okToExitCallback a callback which is called with {@code true} if it's OK to exit
     * and {@code false} if it's not OK.
     */
    public void tryToExit(final Callback<Boolean> okToExitCallback) {
        // This will only be called once even though tryToExitInner will call itself until nothing is unconfirmed.
        WorkspaceStateTracker.create(this).save();

        tryToExitInner(okToExitCallback);
    }

    /**
     * Tries to exit the application.
     *
     * @param okToExitCallback a callback which is called with {@code true} if it's OK to exit
     * and {@code false} if it's not OK.
     */
    public void tryToExitInner(final Callback<Boolean> okToExitCallback) {
        final List<HexFrame> frames = HexFrame.findAllFrames();
        if (frames.isEmpty()) {
            // No frames, can exit immediately.
            okToExitCallback.execute(true);
            return;
        }

        prepareForExit(frames, okToExit -> {
            if (okToExit) {
                for (Frame frame : frames) {
                    frame.dispose();
                }

                // Depending on the platform, the dialogs may have been modeless, so the user might have opened
                // new frames while we were prompting them to close the existing ones.
                SwingUtilities.invokeLater(() -> tryToExitInner(okToExitCallback));
            } else {
                okToExitCallback.execute(false);
            }
        });
    }

    /**
     * Prepares for exiting the application. Recursively calls itself for each frame.
     *
     * @param frames the list of frames.
     * @param okToExitCallback a callback which is called with {@code true} if all frames said it's okay to close
     * or {@code false} if one of them said it wasn't.
     */
    private void prepareForExit(List<HexFrame> frames, final Callback<Boolean> okToExitCallback) {
        if (frames.isEmpty()) {
            // Every frame said it was OK to close.
            okToExitCallback.execute(true);
            return;
        }

        HexFrame firstFrame = frames.get(0);
        final List<HexFrame> remainingFrames = frames.subList(1, frames.size());

        firstFrame.prepareForClose(okToClose -> {
            if (okToClose) {
                // Reducing the risk of a StackOverflowError if there are a large number of frames open.
                SwingUtilities.invokeLater(() -> prepareForExit(remainingFrames, okToExitCallback));
            } else {
                okToExitCallback.execute(false);
            }
        });
    }
}
