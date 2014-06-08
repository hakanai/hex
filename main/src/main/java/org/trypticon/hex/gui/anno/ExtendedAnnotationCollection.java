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

package org.trypticon.hex.gui.anno;

import java.util.Objects;

import org.trypticon.hex.anno.MemoryAnnotationCollection;

/**
 * Extensions to the basic annotation collection.
 *
 * @author trejkaz
 */
public class ExtendedAnnotationCollection extends MemoryAnnotationCollection {
    public ExtendedAnnotationCollection(long length) {
        super(new DefaultExtendedGroupAnnotation(0, length, null));
    }

    public ExtendedAnnotationCollection(ExtendedGroupAnnotation rootGroup) {
        super(rootGroup);
    }

    /**
     * Changes the note on an annotation and fires an event about it.
     *
     * @param annotation the annotation.
     * @param newNote the new note.
     */
    public void changeNote(ExtendedAnnotation annotation, String newNote) {
        String oldNote = annotation.getNote();
        annotation.setNote(newNote);
        if (!Objects.equals(oldNote, newNote)) {
            fireAnnotationChanged(annotation);
        }
    }

    /**
     * Changes the custom style on an annotation and fires an event about it.
     *
     * @param annotation the annotation.
     * @param newCustomStyle the new custom style.
     */
    public void changeCustomStyle(ExtendedAnnotation annotation, ParametricStyle newCustomStyle) {
        ParametricStyle oldCustomStyle = annotation.getCustomStyle();
        annotation.setCustomStyle(newCustomStyle);
        if (!Objects.equals(oldCustomStyle, newCustomStyle)) {
            fireAnnotationChanged(annotation);
        }
    }
}
