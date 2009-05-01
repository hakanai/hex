package org.trypticon.hex.gui.notebook;

import java.io.IOException;
import java.net.URL;
import java.beans.PropertyChangeListener;

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.binary.Binary;

/**
 * TODO: Document this file.
 */
public interface Notebook
{
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

    AnnotationCollection getAnnotations();

    Binary getBinary();

    /**
     * Tests if the notebook is open.
     *
     * @return {@code true} if it is open, {@code false} if it is closed.
     */
    boolean isOpen();

    /**
     * Gets the name of the notebook.  Currently this is derived from the location
     * of the notebook but it might become custom metadata later.
     *
     * This is a bound JavaBeans property.
     *
     * @return the name of the notebook.
     */
    String getName();

    /**
     * Tests if the notebook in this pane has unsaved changes.
     *
     * @return {@code true} if the notebook has unsaved changes.
     */
    boolean isDirty();

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
