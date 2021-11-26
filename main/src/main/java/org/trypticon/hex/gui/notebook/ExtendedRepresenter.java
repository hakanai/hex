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
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.CommonAttributes;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.gui.anno.CustomAttributes;
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

        multiRepresenters.put(Notebook.class, new NotebookRepresenter());
        multiRepresenters.put(GroupAnnotation.class, new GroupAnnotationRepresenter());
        multiRepresenters.put(Annotation.class, new AnnotationRepresenter());
        multiRepresenters.put(Interpreter.class, new InterpreterRepresenter());
        multiRepresenters.put(ParametricStyle.class, new ParametricStyleRepresenter());
    }

    private class NotebookRepresenter implements Represent {
        @Override
        public Node representData(Object object) {
            Notebook notebook = (Notebook) object;
            Map<String, Object> fields = new LinkedHashMap<>(2);
            fields.put("binary_location", notebook.getBinaryLocation().toUri().toString());
            fields.put("root_group", notebook.getAnnotations().getRootGroup());
            return representMapping(YamlTags.NOTEBOOK_TAG, fields, false);
        }
    }

    private class GroupAnnotationRepresenter implements Represent {
        @Override
        public Node representData(Object object) {
            GroupAnnotation groupAnnotation = (GroupAnnotation) object;
            Map<String, Object> fields = new LinkedHashMap<>(2);
            fields.put("position", groupAnnotation.getPosition());
            fields.put("length", groupAnnotation.getLength());
            fields.put("annotations", groupAnnotation.getAnnotations());
            fields.put("note", groupAnnotation.get(CommonAttributes.NOTE));
            fields.put("custom_style", groupAnnotation.get(CustomAttributes.CUSTOM_STYLE));
            return representMapping(YamlTags.GROUP_ANNOTATION_TAG, fields, false);
        }
    }

    private class AnnotationRepresenter implements Represent {
        @Override
        public Node representData(Object object) {
            Annotation annotation = (Annotation) object;
            Map<String, Object> fields = new LinkedHashMap<>(4);
            fields.put("position", annotation.getPosition());
            fields.put("length", annotation.getLength());
            fields.put("interpreter", annotation.getInterpreter());
            fields.put("note", annotation.get(CommonAttributes.NOTE));
            fields.put("custom_style", annotation.get(CustomAttributes.CUSTOM_STYLE));
            return representMapping(YamlTags.ANNOTATION_TAG, fields, false);
        }
    }

    private class InterpreterRepresenter implements Represent {
        @Override
        public Node representData(Object object) {
            Interpreter<?> interpreter = (Interpreter<?>) object;
            Map<String, Object> fields = interpreterStorage.toMap(interpreter);
            return representMapping(YamlTags.INTERPRETER_TAG, fields, true); // true to format on one line
        }
    }

    private class ParametricStyleRepresenter implements Represent {
        @Override
        public Node representData(Object object) {
            ParametricStyle parametricStyle = (ParametricStyle) object;
            Map<String, Object> fields = new LinkedHashMap<>(4);
            fields.put("border_stroke_style", parametricStyle.getBorderStrokeStyle().toString());
            fields.put("border_color", colorToMap(parametricStyle.getBorderColor()));
            fields.put("background_color", colorToMap(parametricStyle.getBackgroundColor()));
            return representMapping(YamlTags.PARAMETRIC_STYLE_TAG, fields, true);
        }
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
