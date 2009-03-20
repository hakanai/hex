/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;

import org.jvyaml.ConstructorImpl;
import org.jvyaml.Composer;
import org.jvyaml.Constructor;
import org.jvyaml.ConstructorException;
import org.jvyaml.nodes.Node;

import org.trypticon.hex.anno.InterpretorStorage;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.MemoryAnnotationCollection;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.Interpretor;

/**
 * Extension of the default representer to dodge some issues in the default one.
 *
 * @author trejkaz
 */
class ExtendedConstructorImpl extends ConstructorImpl {
    private final InterpretorStorage interpretorStorage;
    private final YamlConstructor notebookConstructor = new NotebookConstructor();
    private final YamlConstructor annotationConstructor = new AnnotationConstructor();
    private final YamlConstructor interpretorConstructor = new InterpretorConstructor();

    public ExtendedConstructorImpl(Composer composer, InterpretorStorage interpretorStorage) {
        super(composer);
        this.interpretorStorage = interpretorStorage;
    }

    @Override
    public YamlConstructor getYamlConstructor(Object o) {
        if (YamlTags.NOTEBOOK_TAG.equals(o)) {
            return notebookConstructor;
        } else if (YamlTags.ANNOTATION_TAG.equals(o)) {
            return annotationConstructor;
        } else if (o instanceof String && ((String) o).startsWith(YamlTags.INTERPRETOR_TAG_PREFIX)) {
            return interpretorConstructor;
        } else {
            return super.getYamlConstructor(o);
        }
    }

    private class NotebookConstructor implements YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) constructor.constructMapping(node);

            String binaryLocationURL = (String) map.get("binary_location");
            URL binaryLocation;
            try {
                binaryLocation = new URL(binaryLocationURL);
            } catch (MalformedURLException e) {
                throw new ConstructorException("while constructing notebook", "invalid URL", binaryLocationURL);
            }

            @SuppressWarnings("unchecked")
            List<Annotation> annotationList = (List<Annotation>) map.get("annotations");
            AnnotationCollection annotations = new MemoryAnnotationCollection(annotationList);

            return new Notebook(binaryLocation, annotations);
        }
    }

    private class AnnotationConstructor implements YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) constructor.constructMapping(node);

            long position = (Long) map.get("position");
            Interpretor interpretor = (Interpretor) map.get("interpretor");
            String note = (String) map.get("note");

            return new SimpleMutableAnnotation(position, interpretor, note);
        }
    }

    private class InterpretorConstructor implements YamlConstructor {
        public Object call(Constructor constructor, Node node) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) constructor.constructMapping(node);
            map = new HashMap<String, Object>(map);

            String name = node.getTag().substring(YamlTags.INTERPRETOR_TAG_PREFIX.length());
            map.put("name", name);

            Interpretor interpretor = interpretorStorage.fromMap(map);
            if (interpretor == null) {
                throw new ConstructorException(null, "unknown interpretor name: " + name, null);
            }

            return interpretor;
        }
    }
}
