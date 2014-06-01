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

package org.trypticon.hex.gui.notebook;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.gui.anno.AnnotationExtensions;
import org.trypticon.hex.gui.anno.ParametricStyle;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterStorage;

/**
 * Extension of the default representer to dodge some issues in the default one.
 *
 * @author trejkaz
 */
class ExtendedRepresenter extends Representer {
    private final InterpreterStorage interpreterStorage;

    ExtendedRepresenter(InterpreterStorage interpreterStorage) {
        this.interpreterStorage = interpreterStorage;
    }

    @Override
    protected Node representData(Object data) {

        // Dodge some bug in SnakeYAML: http://code.google.com/p/snakeyaml/issues/detail?id=127
        objectToRepresent = data;

        if (data instanceof Notebook) {
            return representNotebook((Notebook) data);
        } else if (data instanceof GroupAnnotation) {
            return representGroupAnnotation((GroupAnnotation) data);
        } else if (data instanceof Annotation) {
            return representAnnotation((Annotation) data);
        } else if (data instanceof Interpreter) {
            return representInterpreter((Interpreter) data);
        } else if (data instanceof ParametricStyle) {
            return representParametricStyle((ParametricStyle) data);
        } else {
            return super.representData(data);
        }
    }

    private Node representNotebook(Notebook notebook) {
        Map<String, Object> fields = new LinkedHashMap<>(2);
        fields.put("binary_location", notebook.getBinaryLocation().toExternalForm());
        fields.put("root_group", notebook.getAnnotations().getRootGroup());
        return representMapping(YamlTags.NOTEBOOK_TAG, fields, false);
    }

    private Node representGroupAnnotation(GroupAnnotation groupAnnotation) {
        Map<String, Object> fields = new LinkedHashMap<>(2);
        fields.put("position", groupAnnotation.getPosition());
        fields.put("length", groupAnnotation.getLength());
        fields.put("note", groupAnnotation.getNote());
        fields.put("annotations", groupAnnotation.getAnnotations());
        if (groupAnnotation instanceof AnnotationExtensions) {
            fields.put("custom_style", ((AnnotationExtensions) groupAnnotation).getCustomStyle());
        }
        return representMapping(YamlTags.GROUP_ANNOTATION_TAG, fields, false);
    }

    private Node representAnnotation(Annotation annotation) {
        Map<String, Object> fields = new LinkedHashMap<>(4);
        fields.put("position", annotation.getPosition());
        fields.put("length", annotation.getLength());
        fields.put("interpreter", annotation.getInterpreter());
        fields.put("note", annotation.getNote());
        if (annotation instanceof AnnotationExtensions) {
            fields.put("custom_style", ((AnnotationExtensions) annotation).getCustomStyle());
        }
        return representMapping(YamlTags.ANNOTATION_TAG, fields, false);
    }

    private Node representInterpreter(Interpreter interpreter) {
        Map<String, Object> fields = interpreterStorage.toMap(interpreter);
        return representMapping(YamlTags.INTERPRETER_TAG, fields, true); // true to format on one line
    }

    private Node representParametricStyle(ParametricStyle parametricStyle) {
        Map<String, Object> fields = new LinkedHashMap<>(4);
        fields.put("border_stroke_style", parametricStyle.getBorderStrokeStyle().toString());
        fields.put("border_color", colorToMap(parametricStyle.getBorderColor()));
        fields.put("background_color", colorToMap(parametricStyle.getBackgroundColor()));
        return representMapping(YamlTags.PARAMETRIC_STYLE_TAG, fields, true);
    }

    private Map<String, Object> colorToMap(Color color) {
        Map<String, Object> fields = new LinkedHashMap<>(4);
        fields.put("r", color.getRed());
        fields.put("g", color.getGreen());
        fields.put("b", color.getBlue());
        fields.put("a", color.getAlpha());
        return fields;
    }
}
