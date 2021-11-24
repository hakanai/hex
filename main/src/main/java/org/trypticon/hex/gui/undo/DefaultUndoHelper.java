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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * Default implementation of the {@link UndoHelper} interface. Takes care of passing the
 * edits to the {@link UndoManager}.
 *
 * @author trejkaz
 */
class DefaultUndoHelper implements UndoHelper {
    private final UndoManager undoManager = new UndoManager();
    private final DefaultGlobalUndoHelper globalUndoHelper;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private int position = 0;

    DefaultUndoHelper(DefaultGlobalUndoHelper globalUndoHelper) {
        this.globalUndoHelper = globalUndoHelper;
    }

    @Override
    public void perform(DryUndoableEdit edit) throws Exception {
        edit.execute();
        undoManager.addEdit(new UndoableEditAdapter(edit));
        incPosition();
        globalUndoHelper.updateActions();
    }

    @Override
    public void notebookWasSaved() {
        resetPosition();
    }

    @Override
    public boolean isUnsaved() {
        return position != 0;
    }

    private void resetPosition() {
        int oldPosition = position;
        position = 0;
        if (oldPosition != 0) {
            changeSupport.firePropertyChange("unsaved", true, false);
        }
    }
    private void decPosition() {
        position--;
        if (position == 0) {
            changeSupport.firePropertyChange("unsaved", true, false);
        } else if (position == -1) {
            changeSupport.firePropertyChange("unsaved", false, true);
        }
    }

    private void incPosition() {
        position++;
        if (position == 0) {
            changeSupport.firePropertyChange("unsaved", true, false);
        } else if (position == 1) {
            changeSupport.firePropertyChange("unsaved", false, true);
        }
    }

    @Override
    public UndoManager getUndoManager() {
        return undoManager;
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    // Swing's own guidelines say not to use serialisation.
    @SuppressWarnings("serial")
    private class UndoableEditAdapter extends AbstractUndoableEdit {
        private final DryUndoableEdit edit;

        public UndoableEditAdapter(DryUndoableEdit edit) {
            this.edit = edit;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            edit.undo();
            decPosition();
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                edit.execute();
                incPosition();
            } catch (Exception e) {
                // It worked the first time, so it should work the second time - the state is the same.
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getPresentationName() {
            return edit.getPresentationName();
        }
    }
}
