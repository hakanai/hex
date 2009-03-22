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

package org.trypticon.hex.anno;

import javax.swing.event.EventListenerList;

/**
 * Base abstract class for implementing annotation collections.
 *
 * @author trejkaz
 */
public abstract class AbstractAnnotationCollection implements AnnotationCollection {
    private EventListenerList listenerList;

    protected void fireAnnotationsChanged() {
        if (listenerList != null) {
            AnnotationCollectionEvent event = new AnnotationCollectionEvent(this);

            for (AnnotationCollectionListener listener :
                    listenerList.getListeners(AnnotationCollectionListener.class)) {

                listener.annotationsChanged(event);
            }
        }
    }

    public void addAnnotationCollectionListener(AnnotationCollectionListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(AnnotationCollectionListener.class, listener);
    }

    public void removeAnnotationCollectionListener(AnnotationCollectionListener listener) {
        if (listenerList != null) {
            listenerList.remove(AnnotationCollectionListener.class, listener);
        }
    }
}
