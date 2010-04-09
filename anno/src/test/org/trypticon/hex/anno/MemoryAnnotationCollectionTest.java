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
import org.trypticon.hex.interpreters.nulls.NullInterpreter;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link MemoryAnnotationCollection}.
 *
 * @author trejkaz
 */
public class MemoryAnnotationCollectionTest {
    MemoryAnnotationCollection collection;

    @Test
    public void testAddingAnnotationInsideGroupAtStartPosition() throws Exception {
        createCollection(100);

        addGroup(0, 20, "group");
        addLeaf(0, 10, "leaf");

        assertSingleLeafInsideSingleGroup();
    }

    @Test
    public void testAddingGroupAroundAnnotationAtStartPosition() throws Exception {
        createCollection(100);

        addLeaf(0, 10, "leaf");
        addGroup(0, 20, "group");

        assertSingleLeafInsideSingleGroup();
    }

    @Test
    public void testAddingAnnotationInsideGroupAtMiddlePosition() throws Exception {
        createCollection(100);

        addGroup(0, 20, "group");
        addLeaf(5, 10, "leaf");

        assertSingleLeafInsideSingleGroup();
    }

    @Test
    public void testAddingGroupAroundAnnotationAtMiddlePosition() throws Exception {
        createCollection(100);

        addLeaf(5, 10, "leaf");
        addGroup(0, 20, "group");

        assertSingleLeafInsideSingleGroup();
    }

    @Test
    public void testAddingAnnotationInsideGroupAtEndPosition() throws Exception {
        createCollection(100);

        addGroup(0, 20, "group");
        addLeaf(10, 10, "leaf");

        assertSingleLeafInsideSingleGroup();
    }

    @Test
    public void testAddingGroupAroundAnnotationAtEndPosition() throws Exception {
        createCollection(100);

        addLeaf(10, 10, "leaf");
        addGroup(0, 20, "group");

        assertSingleLeafInsideSingleGroup();
    }

    private void assertSingleLeafInsideSingleGroup() {
        assertStructure(new Object[] { null,
                            new Object[] { "group",
                                "leaf"
                            }
                        });
    }

    private void createCollection(int binarySize) {
        collection = new MemoryAnnotationCollection(binarySize);
    }

    private void addGroup(int position, int length, String note) throws Exception {
        GroupAnnotation group = new SimpleMutableGroupAnnotation(position, length, note);
        collection.add(group);
    }

    private void addLeaf(int position, int length, String note) throws Exception {
        Annotation leaf = new SimpleMutableAnnotation(position, length, new NullInterpreter(), note);
        collection.add(leaf);
    }

    private void assertStructure(Object[] expected) {
        assertStructure(collection.getRootGroup(), expected);
    }

    private void assertStructure(GroupAnnotation group, Object[] expected) {
        assertEquals("Wrong node (note didn't match)", expected[0], group.getNote());
        List<Annotation> children = group.getAnnotations();
        assertEquals("Wrong number of children inside " + group, expected.length - 1, children.size());
        for (int i = 1; i < expected.length; i++) {
            Annotation child = children.get(i - 1);
            if (child instanceof GroupAnnotation) {
                assertStructure((GroupAnnotation) child, (Object[]) expected[i]);
            } else {
                assertEquals("Wrong node (note didn't match)", expected[i], child.getNote());
            }
        }
    }
}
