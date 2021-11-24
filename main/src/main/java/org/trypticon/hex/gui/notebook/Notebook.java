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

package org.trypticon.hex.gui.notebook;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.gui.anno.ExtendedAnnotationCollection;

/**
 * Holds information about a single notebook.
 *
 * @author trejkaz
 */
public interface Notebook {
    /**
     * <p>Opens the notebook.  Ultimately this means opening the binary.</p>
     *
     * <p>This is distinct from the constructor as this class can be thought to
     *    be serialisable, even though we are not performing the serialisation
     *    using the normal {@code Serializable} API.</p>
     *
     * @throws IOException if an I/O error prevents opening the binary.
     */
    void open() throws IOException;

    /**
     * Closes the notebook.
     */
    void close();

    /**
     * Gets the location from which the notebook was opened, or to which it was last saved.
     *
     * @return the notebook location.
     */
    @Nullable
    URL getNotebookLocation();

    /**
     * <p>Sets the location of the notebook.</p>
     *
     * <p>Also indicates that the notebook was just saved to that location, thus
     *    sets the dirty flag to {@code false}.</p>
     *
     * @param notebookLocation the new notebook location.
     * @see #getNotebookLocation()
     */
    void setNotebookLocation(URL notebookLocation);

    URL getBinaryLocation();

    ExtendedAnnotationCollection getAnnotations();

    @Nullable
    Binary getBinary();

    /**
     * Tests if the notebook is open.
     *
     * @return {@code true} if it is open, {@code false} if it is closed.
     */
    boolean isOpen();

    /**
     * Adds a listener for property change events.
     *
     * @param propertyName the name of the property to listen to.
     * @param listener the listener to add.
     */
    void addPropertyChangeListener(@NonNls String propertyName, PropertyChangeListener listener);

    /**
     * Removes a listener from property change events.
     *
     * @param propertyName the name of the property to listen to.
     * @param listener the listener to remove.
     */
    void removePropertyChangeListener(@NonNls String propertyName, PropertyChangeListener listener);
}
