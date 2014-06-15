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

package org.trypticon.hex.gui.undo;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.trypticon.hex.gui.HexFrame;
import org.trypticon.hex.gui.NotebookPaneAction;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.ActionException;

/**
 * Implements the global undo tracking.
 *
 * @author trejkaz
 */
public class DefaultGlobalUndoHelper implements GlobalUndoHelper {
    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();

    @Override
    public UndoHelper createUndoHelper() {
        return new DefaultUndoHelper(this);
    }

    @Override
    public Action getUndoAction() {
        return undoAction;
    }

    @Override
    public Action getRedoAction() {
        return redoAction;
    }

    void updateActions() {
        undoAction.updateEnabled();
        redoAction.updateEnabled();
    }

    /**
     * Finds an Undo manager. If no frame is current, returns a throw-away one.
     *
     * @return the Undo manager.
     */
    private UndoManager findUndoManager() {
        HexFrame frame = HexFrame.findActiveFrame();
        if (frame == null) {
            return new UndoManager();
        } else {
            return frame.getNotebookPane().getUndoHelper().getUndoManager();
        }
    }

    private class UndoAction extends NotebookPaneAction {
        private UndoAction() {
            Resources.localiseAction(this, "Undo");
            putValue(NAME, new UndoManager().getUndoPresentationName());
        }

        @Override
        protected void doAction(ActionEvent event, NotebookPane notebookPane) throws ActionException {
            UndoManager undoManager = notebookPane.getUndoHelper().getUndoManager();
            try {
                undoManager.undo();
            } catch (CannotUndoException e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to undo", e);
            }

            updateActions();
        }

        @Override
        protected boolean shouldBeEnabled(NotebookPane notebookPane) {
            return notebookPane.getUndoHelper().getUndoManager().canUndo();
        }

        @Override
        public void setEnabled(boolean newValue) {
            super.setEnabled(newValue);
            putValue(NAME, findUndoManager().getUndoPresentationName());
        }
    }

    private class RedoAction extends NotebookPaneAction {
        private RedoAction() {
            Resources.localiseAction(this, "Redo");
            putValue(NAME, new UndoManager().getRedoPresentationName());
        }

        @Override
        protected void doAction(ActionEvent event, NotebookPane notebookPane) throws ActionException {
            UndoManager undoManager = notebookPane.getUndoHelper().getUndoManager();
            try {
                undoManager.redo();
            } catch (CannotRedoException e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to redo", e);
            }

            updateActions();
        }

        @Override
        protected boolean shouldBeEnabled(NotebookPane notebookPane) {
            return notebookPane.getUndoHelper().getUndoManager().canRedo();
        }

        @Override
        public void setEnabled(boolean newValue) {
            super.setEnabled(newValue);
            putValue(NAME, findUndoManager().getRedoPresentationName());
        }
    }
}
