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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jvyamlb.Representer;
import org.jvyamlb.RepresenterImpl;
import org.jvyamlb.Serializer;
import org.jvyamlb.YAMLConfig;
import org.jvyamlb.YAMLNodeCreator;
import org.jvyamlb.exceptions.RepresenterException;
import org.jvyamlb.nodes.Node;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterStorage;

/**
 * Extension of the default representer to dodge some issues in the default one.
 *
 * @author trejkaz
 */
class ExtendedRepresenterImpl extends RepresenterImpl {
    private final InterpreterStorage interpreterStorage;

    public ExtendedRepresenterImpl(Serializer serializer, YAMLConfig yamlConfig,
                                   InterpreterStorage interpreterStorage) {
        super(serializer, yamlConfig);
        this.interpreterStorage = interpreterStorage;
    }

    @Override
    protected YAMLNodeCreator getNodeCreatorFor(Object o) {
        if (o instanceof DefaultNotebook) {
            return new NotebookYAMLNodeCreator((Notebook) o);
        } else if (o instanceof GroupAnnotation) {
            return new GroupAnnotationYAMLNodeCreator((GroupAnnotation) o);
        } else if (o instanceof Annotation) {
            return new AnnotationYAMLNodeCreator((Annotation) o);
        } else if (o instanceof Interpreter) {
            return new InterpreterYAMLNodeCreator((Interpreter) o);
        } else if (o instanceof List) {
            // Primarily to catch issues with unmodifiable lists.
            return super.getNodeCreatorFor(new ArrayList<Object>((Collection<?>) o));
        } else {
            return super.getNodeCreatorFor(o);
        }
    }

    private class NotebookYAMLNodeCreator implements YAMLNodeCreator {
        private final Notebook notebook;

        private NotebookYAMLNodeCreator(Notebook notebook) {
            this.notebook = notebook;
        }

        public String taguri() {
            return YamlTags.NOTEBOOK_TAG;
        }

        public Node toYamlNode(Representer representer) throws IOException {
            // TODO: Fix JvYAML, it is reordering my LinkedHashMap for no good reason!
            Map<String, Object> fields = new LinkedHashMap<String, Object>(2);
            fields.put("binary_location", notebook.getBinaryLocation().toExternalForm());
            fields.put("root_group", notebook.getAnnotations().getRootGroup());
            return representer.map(taguri(), fields, false);
        }
    }

    private class AnnotationYAMLNodeCreator implements YAMLNodeCreator {
        private final Annotation annotation;

        private AnnotationYAMLNodeCreator(Annotation annotation) {
            this.annotation = annotation;
        }

        public String taguri() {
            return YamlTags.ANNOTATION_TAG;
        }

        public Node toYamlNode(Representer representer) throws IOException {
            Map<String, Object> fields = new LinkedHashMap<String, Object>(4);
            fields.put("position", annotation.getPosition());
            fields.put("length", annotation.getLength());
            fields.put("interpreter", annotation.getInterpreter());
            fields.put("note", annotation.getNote());
            return representer.map(taguri(), fields, false);
        }
    }

    private class GroupAnnotationYAMLNodeCreator implements YAMLNodeCreator {
        private final GroupAnnotation groupAnnotation;

        private GroupAnnotationYAMLNodeCreator(GroupAnnotation groupAnnotation) {
            this.groupAnnotation = groupAnnotation;
        }

        public String taguri() {
            return YamlTags.GROUP_ANNOTATION_TAG;
        }

        public Node toYamlNode(Representer representer) throws IOException {
            Map<String, Object> fields = new LinkedHashMap<String, Object>(2);
            fields.put("position", groupAnnotation.getPosition());
            fields.put("length", groupAnnotation.getLength());
            fields.put("note", groupAnnotation.getNote());
            fields.put("annotations", groupAnnotation.getAnnotations());
            return representer.map(taguri(), fields, false);
        }
    }

    private class InterpreterYAMLNodeCreator implements YAMLNodeCreator {
        private Map<String, Object> options;

        private InterpreterYAMLNodeCreator(Interpreter interpreter) {
            options = interpreterStorage.toMap(interpreter);
            if (options == null) {
                throw new RepresenterException("Unknown interpreter: " + interpreter);
            }
        }

        public String taguri() {
            return YamlTags.INTERPRETER_TAG;
        }

        public Node toYamlNode(Representer representer) throws IOException {
            return representer.map(taguri(), options, true);
        }
    }
}
