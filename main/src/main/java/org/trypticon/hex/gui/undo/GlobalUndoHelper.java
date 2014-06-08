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

import javax.swing.Action;

/**
 * Interface for posting undo events without the need to depend on the whole {@link javax.swing.undo.UndoManager}.
 *
 * @author trejkaz
 */
public interface GlobalUndoHelper {

    /**
     * Creates the undo helper to track undo operations for a single document.
     *
     * @return the undo helper.
     */
    UndoHelper createUndoHelper();

    /**
     * Gets an action to perform Undo operations.
     *
     * @return the action.
     */
    Action getUndoAction();

    /**
     * Gets an action to perform Redo operations.
     *
     * @return the action.
     */
    Action getRedoAction();
}
