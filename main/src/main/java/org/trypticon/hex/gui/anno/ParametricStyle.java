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
import java.util.Objects;

import org.trypticon.hex.AnnotationStyle;

/**
 * <p>An object for configuring an annotation style with a cut-down number of options.</p>
 *
 * <p>The interfaces returned by methods on {@link org.trypticon.hex.AnnotationStyle} are
 *    good for flexibility but without knowing what types of objects are possible in there,
 *    it is impossible to serialise the state.</p>
 *
 * @author trejkaz
 */
public class ParametricStyle {
    private final StrokeStyle borderStrokeStyle;
    private final Color borderColor;
    private final Color backgroundColor;

    public ParametricStyle(StrokeStyle borderStrokeStyle,
                           Color borderColor,
                           Color backgroundColor) {
        this.borderStrokeStyle = borderStrokeStyle;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
    }

    public AnnotationStyle toAnnotationStyle() {
        return new AnnotationStyle(borderStrokeStyle.toStroke(),
                                   borderColor,
                                   backgroundColor);
    }

    public StrokeStyle getBorderStrokeStyle() {
        return borderStrokeStyle;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ParametricStyle)) {
            return false;
        }
        ParametricStyle that = (ParametricStyle) obj;
        return borderStrokeStyle.equals(that.borderStrokeStyle) &&
               borderColor.equals(that.borderColor) &&
               backgroundColor.equals(that.backgroundColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(borderStrokeStyle, borderColor, backgroundColor);
    }

    public static enum StrokeStyle {
        SOLID {
            @Override
            protected Stroke toStroke() {
                return new BasicStroke(1.0f);
            }
        },

        DOTTED {
            @Override
            protected Stroke toStroke() {
                return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                                       10.0f, new float[] { 1.0f, 2.0f }, 0.0f);
            }
        },

        DASHED {
            @Override
            protected Stroke toStroke() {
                return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                                       10.0f, new float[] { 3.0f, 2.0f }, 0.0f);
            }
        };

        protected abstract Stroke toStroke();
    }
}
