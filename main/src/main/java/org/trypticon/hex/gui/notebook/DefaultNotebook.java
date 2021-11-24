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
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;
import org.trypticon.hex.gui.anno.ExtendedAnnotationCollection;
import org.trypticon.hex.util.LoggerUtils;

/**
 * Holds a set of annotations along with a reference to the file the user is working on.
 *
 * @author trejkaz
 */
public class DefaultNotebook implements Notebook {

    @Nullable
    private URL notebookLocation;

    private final URL binaryLocation;

    @Nullable
    private ExtendedAnnotationCollection annotations;

    @Nullable
    private PropertyChangeSupport propertyChanges;

    @Nullable
    private Binary binary;

    private final Object openLock = new Object();

    /**
     * Constructs a new unsaved notebook with a default in-memory annotation collection.
     *
     * @param binaryLocation the location of the binary.
     */
    public DefaultNotebook(URL binaryLocation) {
        this(binaryLocation, null);
    }

    /**
     * Constructs a notebook with an existing annotation collection.
     *
     * @param binaryLocation the location of the binary.
     * @param annotations the annotation collection.
     */
    public DefaultNotebook(URL binaryLocation, @Nullable ExtendedAnnotationCollection annotations) {
        this.binaryLocation = binaryLocation;
        this.annotations = annotations;
    }

    /**
     * <p>Opens the notebook.  Ultimately this means opening the binary.</p>
     *
     * <p>This is distinct from the constructor as this class can be thought to
     *    be serialisable, even though we are not performing the serialisation
     *    using the normal {@code Serializable} API.</p>
     *
     * @throws IOException if an I/O error prevents opening the binary.
     */
    @Override
    public void open() throws IOException {

        // TODO: Support relative path to binary file.

        synchronized (openLock) {
            if (binary != null) {
                LoggerUtils.get().warning("Already open when open() was called, doing nothing.");
            } else {
                binary = BinaryFactory.open(binaryLocation);
            }
        }

        if (annotations == null) {
            // Happens on first creation because we need to know the length of the file.
            annotations = new ExtendedAnnotationCollection(binary.length());
        }
    }

    /**
     * Closes the notebook.
     */
    @Override
    public void close() {
        synchronized (openLock) {
            if (binary == null) {
                LoggerUtils.get().warning("Already closed when close() was called, doing nothing.");
            } else {
                binary.close();
                binary = null;
            }
        }
    }

    /**
     * Gets the location from which the notebook was opened, or to which it was last saved.
     *
     * @return the notebook location.
     */
    @Override
    @Nullable
    public URL getNotebookLocation() {
        return notebookLocation;
    }

    /**
     * <p>Sets the location of the notebook.</p>
     *
     * <p>Also indicates that the notebook was just saved to that location, thus
     *    sets the dirty flag to {@code false}.</p>
     *
     * @param notebookLocation the new notebook location.
     * @see #getNotebookLocation()
     */
    @Override
    public void setNotebookLocation(URL notebookLocation) {
        if (notebookLocation == null) {
            throw new IllegalArgumentException("notebook location cannot be null");
        }

        URL oldNotebookLocation = this.notebookLocation;
        this.notebookLocation = notebookLocation;
        firePropertyChange("notebookLocation", oldNotebookLocation, notebookLocation);
    }

    @Override
    public URL getBinaryLocation() {
        return binaryLocation;
    }

    @Override
    public ExtendedAnnotationCollection getAnnotations() {
        if (annotations == null) {
            throw new IllegalStateException("Notebook has not been opened yet");
        }
        return annotations;
    }

    @Override
    @Nullable
    public Binary getBinary() {
        return binary;
    }

    /**
     * Tests if the notebook is open.
     *
     * @return {@code true} if it is open, {@code false} if it is closed.
     */
    @Override
    public boolean isOpen() {
        return binary != null;
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyChanges == null) {
            propertyChanges = new PropertyChangeSupport(this);
        }
        propertyChanges.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (propertyChanges != null) {
            propertyChanges.removePropertyChangeListener(propertyName, listener);
        }
    }

    private void firePropertyChange(@NonNls String propertyName, @Nullable Object oldValue, @Nullable Object newValue) {
        if (propertyChanges != null) {
            propertyChanges.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
