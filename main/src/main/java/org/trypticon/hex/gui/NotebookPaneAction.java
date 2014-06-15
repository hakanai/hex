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

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.BaseAction;

/**
 * An action which requires the focus on a notebook pane.
 *
 * @author trejkaz
 */
public abstract class NotebookPaneAction extends BaseAction {
    private HexFrame currentHexFrame;
    private NotebookPane currentNotebookPane;

    private PropertyChangeListener notebookPaneListener = event ->
        setNotebookPane((NotebookPane) event.getNewValue());

    public NotebookPaneAction() {
        updateEnabled();

        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener("activeWindow", (event) -> {
            Window activeWindow = (Window) event.getNewValue();
            setHexFrame(activeWindow instanceof HexFrame ? (HexFrame) activeWindow : null);
        });
    }

    private void setHexFrame(HexFrame hexFrame) {
        if (currentHexFrame != null) {
            currentHexFrame.removePropertyChangeListener("notebookPane", notebookPaneListener);
        }

        this.currentHexFrame = hexFrame;

        if (hexFrame != null) {
            setNotebookPane(hexFrame.getNotebookPane());
            hexFrame.addPropertyChangeListener("notebookPane", notebookPaneListener);
        } else {
            setNotebookPane(null);
        }
    }

    private void setNotebookPane(NotebookPane notebookPane) {
        currentNotebookPane = notebookPane;
        updateEnabled();
    }

    @Override
    protected final boolean shouldBeEnabled() {
        return currentNotebookPane != null && shouldBeEnabled(currentNotebookPane);
    }

    protected boolean shouldBeEnabled(NotebookPane notebookPane) {
        return true;
    }

    @Override
    protected final void doAction(ActionEvent event) throws Exception {
        doAction(event, currentNotebookPane);
    }

    protected abstract void doAction(ActionEvent event, NotebookPane notebookPane) throws Exception;
}
