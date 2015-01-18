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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import org.trypticon.hex.AnnotationStyle;
import org.trypticon.hex.DefaultAnnotationStyleScheme;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.interpreters.Value;
import org.trypticon.hex.interpreters.dates.Date;
import org.trypticon.hex.interpreters.dates.DateTime;
import org.trypticon.hex.interpreters.dates.Time;
import org.trypticon.hex.interpreters.nulls.NullValue;
import org.trypticon.hex.interpreters.primitives.floating.Float128;
import org.trypticon.hex.interpreters.primitives.floating.Float16;
import org.trypticon.hex.interpreters.primitives.floating.Float32;
import org.trypticon.hex.interpreters.primitives.floating.Float64;
import org.trypticon.hex.interpreters.primitives.signed.SByte;
import org.trypticon.hex.interpreters.primitives.signed.SInt;
import org.trypticon.hex.interpreters.primitives.signed.SLong;
import org.trypticon.hex.interpreters.primitives.signed.SShort;
import org.trypticon.hex.interpreters.primitives.unsigned.UByte;
import org.trypticon.hex.interpreters.primitives.unsigned.UInt;
import org.trypticon.hex.interpreters.primitives.unsigned.ULong;
import org.trypticon.hex.interpreters.primitives.unsigned.UShort;
import org.trypticon.hex.interpreters.strings.StringValue;

/**
 * Customised annotation style scheme to allow for more style customisation.
 *
 * @author trejkaz
 */
public class CustomAnnotationStyleScheme extends DefaultAnnotationStyleScheme {
    private final Map<Class<? extends Value>, AnnotationStyle> styleMap = new HashMap<>();

    public CustomAnnotationStyleScheme() {
        Stroke basicStroke = new BasicStroke(1.0f);

        // Unknown values
        styleMap.put(NullValue.class, new AnnotationStyle(
            basicStroke, new Color(204, 204, 204).darker(), new Color(204, 204, 204)));

        // Signed integers
        AnnotationStyle signedStyle = new AnnotationStyle(
            basicStroke, new Color(153, 255, 255).darker(), new Color(153, 255, 255));
        styleMap.put(SByte.class, signedStyle);
        styleMap.put(SShort.class, signedStyle);
        styleMap.put(SInt.class, signedStyle);
        styleMap.put(SLong.class, signedStyle);

        // Unsigned integers
        AnnotationStyle unsignedStyle = new AnnotationStyle(
            basicStroke, new Color(153, 255, 153).darker(), new Color(153, 255, 153));
        styleMap.put(UByte.class, unsignedStyle);
        styleMap.put(UShort.class, unsignedStyle);
        styleMap.put(UInt.class, unsignedStyle);
        styleMap.put(ULong.class, unsignedStyle);

        // Floating point
        AnnotationStyle floatStyle = new AnnotationStyle(
            basicStroke, new Color(255, 153, 255).darker(), new Color(255, 153, 255));
        styleMap.put(Float16.class, floatStyle);
        styleMap.put(Float32.class, floatStyle);
        styleMap.put(Float64.class, floatStyle);
        styleMap.put(Float128.class, floatStyle);

        // Dates and times
        AnnotationStyle dateStyle = new AnnotationStyle(
            basicStroke, new Color(255, 153, 153).darker(), new Color(255, 153, 153));
        styleMap.put(Date.class, dateStyle);
        styleMap.put(Time.class, dateStyle);
        styleMap.put(DateTime.class, dateStyle);

        // Strings
        styleMap.put(StringValue.class, new AnnotationStyle(
            basicStroke, new Color(255, 255, 153).darker(), new Color(255, 255, 153)));
    }

    @Override
    public AnnotationStyle getStyle(Annotation annotation) {
        ParametricStyle customStyle = annotation.get(CustomAttributes.CUSTOM_STYLE);
        if (customStyle != null) {
            return customStyle.toAnnotationStyle();
        }

        if (annotation instanceof GroupAnnotation) {
            return super.getStyle(annotation);
        } else {
            Class<? extends Value> type = annotation.getInterpreter().getType();
            AnnotationStyle style = styleMap.get(type);
            if (style == null) {
                style = computeStyle(type);
                if (style == null) {
                    style = super.getStyle(annotation); // last resort
                }
                styleMap.put(type, style);
            }
            return style;
        }
    }

    /**
     * Slower method to compute the type for a given value class by searching all superclasses
     * and interfaces.
     *
     * @param type the type.
     * @return the computed style or {@code null} if it could not be computed.
     */
    @Nullable
    private AnnotationStyle computeStyle(Class<? extends Value> type) {
        AnnotationStyle style = styleMap.get(type);
        if (style != null) {
            return style;
        }

        Class<?> superclass = type.getSuperclass();
        if (superclass != null && Value.class.isAssignableFrom(superclass)) {
            style = computeStyle(superclass.asSubclass(Value.class));
            if (style != null) {
                return style;
            }
        }

        for (Class<?> face : type.getInterfaces()) {
            if (superclass != null && Value.class.isAssignableFrom(face)) {
                style = computeStyle(superclass.asSubclass(Value.class));
                if (style != null) {
                    return style;
                }
            }
        }

        return null;
    }
}
