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
import java.util.Objects;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;

/**
 * Group annotation with additional methods.
 *
 * @author trejkaz
 */
public class ExtendedGroupAnnotation extends SimpleMutableGroupAnnotation implements AnnotationExtensions {
    private ParametricStyle customStyle;

    public ExtendedGroupAnnotation(long position, long length, String note) {
        this(position, length, note, new ArrayList<>(4));
    }

    public ExtendedGroupAnnotation(long position, long length, String note, List<Annotation> annotations) {
        this(position, length, note, annotations, null);
    }

    public ExtendedGroupAnnotation(long position, long length, String note, List<Annotation> annotations,
                                   ParametricStyle customStyle) {
        super(position, length, note, annotations);
        this.customStyle = customStyle;
    }

    @Override
    public ParametricStyle getCustomStyle() {
        return customStyle;
    }

    @Override
    public void setCustomStyle(ParametricStyle customStyle) {
        this.customStyle = customStyle;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ExtendedGroupAnnotation)) {
            return false;
        }
        ExtendedGroupAnnotation that = (ExtendedGroupAnnotation) o;
        return super.equals(that) &&
               Objects.equals(getCustomStyle(), that.getCustomStyle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCustomStyle());
    }
}
