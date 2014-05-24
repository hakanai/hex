/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;
import org.trypticon.hex.util.LoggerUtils;
import org.trypticon.hex.util.URLUtils;

/**
 * Holds a set of annotations along with a reference to the file the user is working on.
 *
 * @author trejkaz
 */
public class DefaultNotebook implements Notebook {

    private PropertyChangeSupport propertyChanges;
    private URL notebookLocation;
    private String name;
    private final URL binaryLocation;
    private AnnotationCollection annotations;
    private Binary binary;

    private final Object openLock = new Object();

    /**
     * Dirty flag.  Set to {@code true} while the notebook has unsaved changes.
     */
    private boolean dirty;

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
    public DefaultNotebook(URL binaryLocation, AnnotationCollection annotations) {
        this.binaryLocation = binaryLocation;
        this.annotations = annotations;

        if (annotations != null) {
            attachAnnotationCollectionListener();
        }

        String path = binaryLocation.getPath();
        int lastSlash = path.lastIndexOf('/');
        String baseName = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        baseName = URLUtils.decode(baseName);

        this.name = "New: " + baseName;

        // Presumed dirty until someone sets the location.
        setDirty(true);
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
            annotations = new MemoryAnnotationCollection(binary.length());

            attachAnnotationCollectionListener();
        }
    }

    private void attachAnnotationCollectionListener() {
        // New annotations appearing mean we need to be saved.
        annotations.addAnnotationCollectionListener(event -> {
            setDirty(true);
        });
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

        if (!notebookLocation.equals(this.notebookLocation)) {
            this.notebookLocation = notebookLocation;

            String path = notebookLocation.getPath();
            int lastSlash = path.lastIndexOf('/');
            String baseName = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
            baseName = URLUtils.decode(baseName);

            setName(baseName);
        }

        setDirty(false);
    }

    @Override
    public URL getBinaryLocation() {
        return binaryLocation;
    }

    @Override
    public AnnotationCollection getAnnotations() {
        return annotations;
    }

    @Override
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

    /**
     * <p>Gets the name of the notebook.  Currently this is derived from the location
     *    of the notebook but it might become custom metadata later.</p>
     *
     * <p>This is a bound JavaBeans property.</p>
     *
     * @return the name of the notebook.
     */
    @Override
    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (!name.equals(this.name)) {
            String oldName = this.name;
            this.name = name;
            firePropertyChange("name", oldName, name);
            setDirty(true);
        }
    }

    /**
     * Tests if the notebook in this pane has unsaved changes.
     *
     * @return {@code true} if the notebook has unsaved changes.
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    private void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            boolean oldDirty = this.dirty;
            this.dirty = dirty;
            firePropertyChange("dirty", oldDirty, dirty);
        }
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

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChanges != null) {
            propertyChanges.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
