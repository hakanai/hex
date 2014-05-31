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

import org.trypticon.hex.AnnotationStyle;
import org.trypticon.hex.DefaultAnnotationStyleScheme;
import org.trypticon.hex.anno.Annotation;

/**
 * Customised annotation style scheme to allow for more style customisation.
 *
 * @author trejkaz
 */
public class CustomAnnotationStyleScheme extends DefaultAnnotationStyleScheme {
    @Override
    public AnnotationStyle getStyle(Annotation annotation) {
        if (annotation instanceof ExtendedAnnotation) {
            ParametricStyle customStyle = ((ExtendedAnnotation) annotation).getCustomStyle();
            if (customStyle != null) {
                return customStyle.toAnnotationStyle();
            }
        }

        //TODO: A configurable colour scheme based on the interpreter type would be nice too.

        return super.getStyle(annotation);
    }
}
