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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import org.jdesktop.swingx.renderer.JRendererLabel;

import org.trypticon.hex.AnnotationStyle;
import org.trypticon.hex.AnnotationStyleScheme;
import org.trypticon.hex.anno.Annotation;

/**
 * A component to use for rendering which shows the annotation style.
 * Also used for the editor, to keep a consistent look.
 *
 * @author trejkaz
 */
class ParametricStyleRendererComponent extends JRendererLabel {
    private final AnnotationStyleScheme annotationStyleScheme;

    private Annotation annotation;
    private AnnotationStyle annotationStyle;
    private ParametricStyle parametricStyle;

    ParametricStyleRendererComponent(AnnotationStyleScheme annotationStyleScheme) {
        setOpaque(true);
        this.annotationStyleScheme = annotationStyleScheme;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
        annotationStyle = annotationStyleScheme.getStyle(annotation);
    }

    public ParametricStyle getParametricStyle() {
        return parametricStyle;
    }

    public void setParametricStyle(ParametricStyle parametricStyle) {
        if (parametricStyle != null) {
            annotationStyle = parametricStyle.toAnnotationStyle();
        } else {
            annotationStyle = annotationStyleScheme.getStyle(annotation);
        }
        paintImmediately(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (annotation == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        Shape shape = new Rectangle(2, 2, width - 5, height - 5);

        g2.setPaint(annotationStyle.getBackgroundPaint());
        g2.fill(shape);
        g2.setStroke(annotationStyle.getBorderStroke());
        g2.setPaint(annotationStyle.getBorderPaint());
        g2.draw(shape);
    }
}
