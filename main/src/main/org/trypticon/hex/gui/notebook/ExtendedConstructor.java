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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.gui.anno.ExtendedAnnotation;
import org.trypticon.hex.gui.anno.ExtendedGroupAnnotation;
import org.trypticon.hex.gui.anno.ParametricStyle;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterStorage;

/**
 * Extension of the default representer to dodge some issues in the default one.
 *
 * @author trejkaz
 */
class ExtendedConstructor extends Constructor {
    private final InterpreterStorage interpreterStorage;

    public ExtendedConstructor(InterpreterStorage interpreterStorage) {
        this.interpreterStorage = interpreterStorage;

        yamlConstructors.put(YamlTags.NOTEBOOK_TAG, new NotebookConstructor());
        yamlConstructors.put(YamlTags.GROUP_ANNOTATION_TAG, new GroupAnnotationConstructor());
        yamlConstructors.put(YamlTags.ANNOTATION_TAG, new AnnotationConstructor());
        yamlConstructors.put(YamlTags.INTERPRETER_TAG, new InterpreterConstructor());
        yamlConstructors.put(YamlTags.PARAMETRIC_STYLE_TAG, new ParametricStyleConstructor());
    }

    private abstract class SimpleConstructor implements Construct {
        protected abstract Object construct(Map<Object, Object> map);

        @Override
        public Object construct(Node node) {
            return construct(constructMapping((MappingNode) node));
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
        }
    }

    private class NotebookConstructor extends SimpleConstructor {
        @Override
        protected Object construct(Map<Object, Object> map) {
            String binaryLocationURL = (String) map.get("binary_location");
            URL binaryLocation;
            try {
                binaryLocation = new URL(binaryLocationURL);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid URL: " + binaryLocationURL);
            }

            GroupAnnotation rootGroup = (GroupAnnotation) map.get("root_group");

            AnnotationCollection annotations = new MemoryAnnotationCollection(rootGroup);

            return new DefaultNotebook(binaryLocation, annotations);
        }

    }

    private class GroupAnnotationConstructor extends SimpleConstructor {
        @Override
        protected Object construct(Map<Object, Object> map) {
            long position = ((Number) map.get("position")).longValue();
            int length = ((Number) map.get("length")).intValue();
            String note = (String) map.get("note");
            @SuppressWarnings("unchecked")
            List<Annotation> annotations = (List<Annotation>) map.get("annotations");
            ParametricStyle customStyle = (ParametricStyle) map.get("custom_style");

            return new ExtendedGroupAnnotation(position, length, note, annotations, customStyle);
        }
    }

    private class AnnotationConstructor extends SimpleConstructor {
        @Override
        protected Object construct(Map<Object, Object> map) {
            long position = ((Number) map.get("position")).longValue();
            int length = ((Number) map.get("length")).intValue();
            Interpreter interpreter = (Interpreter) map.get("interpreter");
            String note = (String) map.get("note");
            ParametricStyle customStyle = (ParametricStyle) map.get("custom_style");

            return new ExtendedAnnotation(position, length, interpreter, note, customStyle);
        }
    }

    private class InterpreterConstructor extends SimpleConstructor {
        @Override
        protected Object construct(Map<Object, Object> map) {
            Map<String, Object> sanitisedMap = new LinkedHashMap<>(map.size());
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                sanitisedMap.put((String) entry.getKey(), entry.getValue());
            }

            Interpreter interpreter = interpreterStorage.fromMap(sanitisedMap);
            if (interpreter == null) {
                throw new IllegalArgumentException("Unknown interpreter: " + map);
            }

            return interpreter;
        }
    }

    private class ParametricStyleConstructor extends SimpleConstructor {
        @Override
        protected Object construct(Map<Object, Object> map) {
            String borderStrokeStyleString = (String) map.get("border_stroke_style");
            ParametricStyle.StrokeStyle borderStrokeStyle =
                borderStrokeStyleString == null ? null : ParametricStyle.StrokeStyle.valueOf(borderStrokeStyleString);
            Color borderColor = mapToColor((Map<?, ?>) map.get("border_color"));
            Color backgroundColor = mapToColor((Map<?, ?>) map.get("background_color"));

            return new ParametricStyle(borderStrokeStyle, borderColor, backgroundColor);
        }
    }

    private Color mapToColor(Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        int r = ((Number) map.get("r")).intValue();
        int g = ((Number) map.get("g")).intValue();
        int b = ((Number) map.get("b")).intValue();
        int a = ((Number) map.get("a")).intValue();
        return new Color(r, g, b, a);
    }
}
