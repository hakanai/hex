/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

import java.net.URL;
import java.io.IOException;
import java.util.logging.Logger;

import org.trypticon.binary.Binary;
import org.trypticon.binary.BinaryFactory;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.MemoryAnnotationCollection;

/**
 * Holds a set of annotations along with a reference to the file the user is working on.
 *
 * TODO: I want a better name for this class.  It's a document, but the user won't see
 *       it as a document.  What do you call a collection of annotations?
 *
 * @author trejkaz
 */
public class Notebook {
    private static final Logger logger = Logger.getLogger(Notebook.class.getName());
    private URL notebookLocation;
    private final URL binaryLocation;
    private final AnnotationCollection annotations;
    private Binary binary;

    private final Object openLock = new Object();

    /**
     * Constructs a new unsaved notebook with a default in-memory annotation collection.
     *
     * @param binaryLocation the location of the binary.
     */
    public Notebook(URL binaryLocation) {
        this(binaryLocation, new MemoryAnnotationCollection());
    }

    /**
     * Constructs a notebook with an existing annotation collection.
     *
     * @param binaryLocation the location of the bianry.
     * @param annotations the annotation collection.
     */
    public Notebook(URL binaryLocation, AnnotationCollection annotations) {
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
    public void open() throws IOException {

        // TODO: Support relative path to binary file.

        synchronized (openLock) {
            if (binary != null) {
                logger.warning("Already open when open() was called, doing nothing.");
            } else {
                binary = BinaryFactory.open(binaryLocation);
            }
        }
    }

    /**
     * Closes the notebook.
     */
    public void close() {
        synchronized (openLock) {
            if (binary == null) {
                logger.warning("Already closed when close() was called, doing nothing.");
            } else {
                binary.close();
            }
        }
    }

    /**
     * Gets the location from which the notebook was opened, or to which it was last saved.
     *
     * @return the notebook location.
     */
    public URL getNotebookLocation() {
        return notebookLocation;
    }

    /**
     * Sets the location of the notebook.
     *
     * @param notebookLocation the new notebook location.
     * @see #getNotebookLocation()
     */
    public void setNotebookLocation(URL notebookLocation) {
        this.notebookLocation = notebookLocation;
    }

    public URL getBinaryLocation() {
        return binaryLocation;
    }

    public AnnotationCollection getAnnotations() {
        return annotations;
    }

    public Binary getBinary() {
        return binary;
    }
}
