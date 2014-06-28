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
import java.lang.Exception;
import javax.swing.undo.UndoManager;

import org.jetbrains.annotations.NonNls;

/**
 * Interface for posting undo events without the need to depend on the whole {@link UndoManager}.
 *
 * @author trejkaz
 */
public interface UndoHelper {
    /**
     * Performs an undoable edit and adds the edit to the list of undoable edits.
     *
     * @param edit the edit.
     * @throws Exception if the edit throws an exception.
     */
    void perform(DryUndoableEdit edit) throws Exception;

    /**
     * Called from the outside to indicate that the notebook has been saved.
     */
    void notebookWasSaved();

    /**
     * Tests if the notebook is currently unsaved.
     *
     * @return {@code true} if the notebook is unsaved, {@code false} otherwise.
     */
    boolean isUnsaved();

    /**
     * Gets the undo manager used by this helper.
     *
     * @return the undo manager used by this helper.
     */
    UndoManager getUndoManager();

    void addPropertyChangeListener(@NonNls String propertyName, PropertyChangeListener listener);
    void removePropertyChangeListener(@NonNls String propertyName, PropertyChangeListener listener);
}
