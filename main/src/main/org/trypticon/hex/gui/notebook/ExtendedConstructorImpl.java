/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jruby.util.ByteList;
import org.jvyamlb.Composer;
import org.jvyamlb.Constructor;
import org.jvyamlb.ConstructorImpl;
import org.jvyamlb.exceptions.ConstructorException;
import org.jvyamlb.nodes.Node;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.SimpleMutableGroupAnnotation;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterStorage;

/**
 * Extension of the default representer to dodge some issues in the default one.
 *
 * @author trejkaz
 */
class ExtendedConstructorImpl extends ConstructorImpl {
    private final InterpreterStorage interpreterStorage;
    private final YamlConstructor notebookConstructor = new NotebookConstructor();
    private final YamlConstructor annotationConstructor = new AnnotationConstructor();
    private final YamlConstructor annotationGroupConstructor = new GroupAnnotationConstructor();
    private final YamlConstructor interpreterConstructor = new InterpreterConstructor();

    public ExtendedConstructorImpl(Composer composer, InterpreterStorage interpreterStorage) {
        super(composer);
        this.interpreterStorage = interpreterStorage;
    }

    /**
     * Converts a map with {@code ByteList} keys back into a map with {@code String} keys.
     *
     * @param map the map.
     * @return the fixed map.
     */
    private Map<String, Object> fixMapping(Map<ByteList, ?> map) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<ByteList, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof ByteList) {
                value = value.toString();
            }

            result.put(key, value);
        }
        return result;
    }

    @Override
    public YamlConstructor getYamlConstructor(Object o) {
        if (YamlTags.NOTEBOOK_TAG.equals(o)) {
            return notebookConstructor;
        } else if (YamlTags.GROUP_ANNOTATION_TAG.equals(o)) {
            return annotationGroupConstructor;
        } else if (YamlTags.ANNOTATION_TAG.equals(o)) {
            return annotationConstructor;
        } else if (YamlTags.INTERPRETER_TAG.equals(o)) {
            return interpreterConstructor;
        } else {
            return super.getYamlConstructor(o);
        }
    }

    private class NotebookConstructor implements Constructor.YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = fixMapping((Map<ByteList, ?>) constructor.constructMapping(node));

            String binaryLocationURL = (String) map.get("binary_location");
            URL binaryLocation;
            try {
                binaryLocation = new URL(binaryLocationURL);
            } catch (MalformedURLException e) {
                throw new ConstructorException("while constructing notebook", "invalid URL", binaryLocationURL);
            }

            GroupAnnotation rootGroup = (GroupAnnotation) map.get("root_group");

            AnnotationCollection annotations = new MemoryAnnotationCollection(rootGroup);

            return new DefaultNotebook(binaryLocation, annotations);
        }
    }

    private class AnnotationConstructor implements YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = fixMapping((Map<ByteList, ?>) constructor.constructMapping(node));

            long position = ((Number) map.get("position")).longValue();
            int length = ((Number) map.get("length")).intValue();
            Interpreter interpreter = (Interpreter) map.get("interpreter");
            String note = (String) map.get("note");

            return new SimpleMutableAnnotation(position, length, interpreter, note);
        }
    }

    private class GroupAnnotationConstructor implements YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = fixMapping((Map<ByteList, ?>) constructor.constructMapping(node));

            long position = ((Number) map.get("position")).longValue();
            int length = ((Number) map.get("length")).intValue();
            String note = (String) map.get("note");
            @SuppressWarnings("unchecked")
            List<Annotation> annotations = (List<Annotation>) map.get("annotations");

            return new SimpleMutableGroupAnnotation(position, length, note, annotations);
        }
    }

    private class InterpreterConstructor implements YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = fixMapping((Map<ByteList, ?>) constructor.constructMapping(node));

            Interpreter interpreter = interpreterStorage.fromMap(map);
            if (interpreter == null) {
                throw new ConstructorException(null, "unknown interpreter: " + map, null);
            }

            return interpreter;
        }
    }
}
