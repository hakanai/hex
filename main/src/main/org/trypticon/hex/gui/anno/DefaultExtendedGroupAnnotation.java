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

import java.util.ArrayList;
import java.util.List;

import org.trypticon.hex.anno.MutableAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;

/**
 * Group annotation with additional methods.
 *
 * @author trejkaz
 */
public class DefaultExtendedGroupAnnotation extends SimpleMutableGroupAnnotation implements ExtendedGroupAnnotation {
    private ParametricStyle customStyle;

    public DefaultExtendedGroupAnnotation(long position, long length, String note) {
        this(position, length, note, new ArrayList<>(4));
    }

    public DefaultExtendedGroupAnnotation(long position, long length, String note,
                                          List<? extends DefaultExtendedGroupAnnotation> annotations) {
        this(position, length, note, annotations, null);
    }

    public DefaultExtendedGroupAnnotation(long position, long length, String note,
                                          List<? extends ExtendedAnnotation> annotations,
                                          ParametricStyle customStyle) {
        super(position, length, note, annotations);
        this.customStyle = customStyle;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? extends ExtendedAnnotation> getAnnotations() {
        // Safe cast, guarded by add().
        return (List<? extends ExtendedAnnotation>) super.getAnnotations();
    }

    @Override
    public int add(MutableAnnotation annotation) {
        if (!(annotation instanceof ExtendedAnnotation)) {
            throw new IllegalArgumentException("Annotation should be an ExtendedAnnotation but wasn't: " + annotation);
        }
        return super.add(annotation);
    }

    @Override
    public ParametricStyle getCustomStyle() {
        return customStyle;
    }

    @Override
    public void setCustomStyle(ParametricStyle customStyle) {
        this.customStyle = customStyle;
    }
}
