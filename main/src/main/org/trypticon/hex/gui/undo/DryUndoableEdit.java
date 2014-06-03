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

import java.lang.Exception;
import javax.swing.undo.UndoableEdit;

/**
 * Interface for undoable operations. Unlike the Swing {@link UndoableEdit} interface, the {@link #execute()}
 * method here can be done to perform the edit itself, removing the need to write the same code twice.
 *
 * @author trejkaz
 */
public interface DryUndoableEdit {

    /**
     * Does the edit. I would call this {@code do}, but it's a keyword.
     *
     * @throws Exception if an error occurs.
     */
    void execute() throws Exception;

    /**
     * Undoes the edit.
     */
    void undo();

    /**
     * Gets a user-readable description of the action. Used in the action text (if it returns "Some Edit")
     * then the action will be named "Undo Some Edit".
     *
     * @return the presentation name.
     */
    String getPresentationName();

}
