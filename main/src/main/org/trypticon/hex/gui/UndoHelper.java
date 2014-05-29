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

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.trypticon.hex.gui.notebook.NotebookPane;
import org.trypticon.hex.gui.util.ActionException;

/**
 * Helper to bring the Undo functionality into a single location so that each action can update the other.
 *
 * @author trejkaz
 */
public class UndoHelper {
    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();

    public Action getUndoAction() {
        return undoAction;
    }

    public Action getRedoAction() {
        return redoAction;
    }

    public void updateActions() {
        undoAction.updateEnabled();
        redoAction.updateEnabled();
    }

    private class UndoAction extends NotebookPaneAction {
        private UndoAction() {
            Resources.localiseAction(this, "Undo");
        }

        @Override
        protected void doAction(ActionEvent event, NotebookPane notebookPane) throws ActionException {
            UndoManager undoManager = notebookPane.getUndoManager();

            try {
                undoManager.undo();
            } catch (CannotUndoException e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to undo", e);
            }
            updateEnabled();
            redoAction.updateEnabled();
        }

        @Override
        protected boolean shouldBeEnabled(NotebookPane notebookPane) {
            UndoManager undoManager = notebookPane.getUndoManager();
            if (undoManager.canUndo()) {
                putValue(NAME, undoManager.getUndoPresentationName());
                return true;
            } else {
                putValue(NAME, Resources.getString("Undo.name"));
                return false;
            }
        }
    }

    private class RedoAction extends NotebookPaneAction {
        private RedoAction() {
            Resources.localiseAction(this, "Redo");
        }

        @Override
        protected void doAction(ActionEvent event, NotebookPane notebookPane) throws ActionException {
            UndoManager undoManager = notebookPane.getUndoManager();

            try {
                undoManager.redo();
            } catch (CannotRedoException e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "Unable to redo", e);
            }

            updateEnabled();
            undoAction.updateEnabled();
        }

        @Override
        protected boolean shouldBeEnabled(NotebookPane notebookPane) {
            UndoManager undoManager = notebookPane.getUndoManager();
            if (undoManager.canRedo()) {
                putValue(NAME, undoManager.getRedoPresentationName());
                return true;
            } else {
                putValue(NAME, Resources.getString("Redo.name"));
                return false;
            }
        }
    }
}
