/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Model maintaining the range of selected binary in the viewer.
 *
 * @author trejkaz
 */
public class HexViewerSelectionModel {
    private EventListenerList listenerList;

    private long selectionStart;
    private long selectionEnd;
    private long cursor;

    /**
     * Gets the start of the selection.
     *
     * @return the start of the selection, inclusive.
     */
    public long getSelectionStart() {
        return selectionStart;
    }

    /**
     * Gets the end of the selection.
     *
     * @return the end of the selection, inclusive.
     */
    public long getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * Gets the position of the cursor.
     *
     * @return the position of the cursor.
     */
    public long getCursor() {
        return cursor;
    }

    /**
     * Sets the position of the cursor.
     *
     * @param cursor the position of the cursor.
     */
    public void setCursor(long cursor) {
        if (this.cursor != cursor || selectionStart != cursor || selectionEnd != cursor) {
            this.cursor = cursor;
            selectionStart = cursor;
            selectionEnd = cursor;
            fireStateChanged();
        }
    }

    /**
     * Sets the position of the cursor, extending the selection to move to where the cursor lands.
     *
     * @param cursor the new position of the cursor.
     */
    public void setCursorAndExtendSelection(long cursor) {
        if (this.cursor != cursor) {
            // Finds which end anchors the selection.
            long anchor = this.cursor == selectionStart ? selectionEnd : selectionStart;

            this.cursor = cursor;
            selectionStart = Math.min(anchor, cursor);
            selectionEnd = Math.max(anchor, cursor);

            fireStateChanged();
        }
    }

    /**
     * Adds a listener for changes in the model.
     *
     * @param listener the listener to add.
     */
    public void addChangeListener(ChangeListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(ChangeListener.class, listener);
    }

    /**
     * Removes a listener from changes in the model.
     *
     * @param listener the listener to remove.
     */
    public void removeChangeListener(ChangeListener listener) {
        if (listenerList != null) {
            listenerList.remove(ChangeListener.class, listener);
        }
    }

    /**
     * Fires a {@code stateChanged} event to all listeners.
     */
    private void fireStateChanged() {
        if (listenerList != null) {
            ChangeEvent event = new ChangeEvent(this);
            for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
                listener.stateChanged(event);
            }
        }
    }
}
