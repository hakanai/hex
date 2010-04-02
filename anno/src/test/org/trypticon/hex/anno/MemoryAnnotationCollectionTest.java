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

package org.trypticon.hex.anno;

import org.junit.Test;
import org.trypticon.hex.anno.nulls.NullInterpretor;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link MemoryAnnotationCollection}.
 *
 * @author trejkaz
 */
public class MemoryAnnotationCollectionTest {
    @Test
    public void testAddingAnnotationInsideGroupAtStartPosition() throws Exception {
        MemoryAnnotationCollection collection = new MemoryAnnotationCollection(100);

        GroupAnnotation group = new SimpleMutableGroupAnnotation(0, 20, "group");
        Annotation leaf = new SimpleMutableAnnotation(0, 10, new NullInterpretor(), "leaf");

        collection.add(group);
        collection.add(leaf);

        List<Annotation> topLevel = collection.getTopLevel();
        assertEquals("Group should be by itself at the top level", Arrays.<Annotation>asList(group), topLevel);

        List<Annotation> groupLevel = group.getAnnotations();
        assertEquals("Leaf should be by itself at the group level", Arrays.<Annotation>asList(leaf), groupLevel);
    }

    @Test
    public void testAddingAnnotationInsideGroupAtMiddlePosition() throws Exception {
        MemoryAnnotationCollection collection = new MemoryAnnotationCollection(100);

        GroupAnnotation group = new SimpleMutableGroupAnnotation(0, 20, "group");
        Annotation leaf = new SimpleMutableAnnotation(5, 10, new NullInterpretor(), "leaf");

        collection.add(group);
        collection.add(leaf);

        List<Annotation> topLevel = collection.getTopLevel();
        assertEquals("Group should be by itself at the top level", Arrays.<Annotation>asList(group), topLevel);

        List<Annotation> groupLevel = group.getAnnotations();
        assertEquals("Leaf should be by itself at the group level", Arrays.<Annotation>asList(leaf), groupLevel);
    }

    @Test
    public void testAddingAnnotationInsideGroupAtEndPosition() throws Exception {
        MemoryAnnotationCollection collection = new MemoryAnnotationCollection(100);

        GroupAnnotation group = new SimpleMutableGroupAnnotation(0, 20, "group");
        Annotation leaf = new SimpleMutableAnnotation(10, 10, new NullInterpretor(), "leaf");

        collection.add(group);
        collection.add(leaf);

        List<Annotation> topLevel = collection.getTopLevel();
        assertEquals("Group should be by itself at the top level", Arrays.<Annotation>asList(group), topLevel);

        List<Annotation> groupLevel = group.getAnnotations();
        assertEquals("Leaf should be by itself at the group level", Arrays.<Annotation>asList(leaf), groupLevel);
    }
}
