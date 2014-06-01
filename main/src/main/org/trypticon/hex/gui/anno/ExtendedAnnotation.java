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

import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.interpreters.Interpreter;

/**
 * Annotation with additional methods.
 *
 * @author trejkaz
 */
public class ExtendedAnnotation extends SimpleMutableAnnotation implements AnnotationExtensions {
    private ParametricStyle customStyle;

    public ExtendedAnnotation(long position, long length, Interpreter interpreter, String note) {
        this(position, length, interpreter, note, null);
    }

    public ExtendedAnnotation(long position, long length, Interpreter interpreter, String note,
                              ParametricStyle customStyle) {
        super(position, length, interpreter, note);
        setCustomStyle(customStyle);
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
        if (!(o instanceof ExtendedAnnotation)) {
            return false;
        }
        ExtendedAnnotation that = (ExtendedAnnotation) o;
        return super.equals(that) &&
               Objects.equals(getCustomStyle(), that.getCustomStyle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCustomStyle());
    }
}
