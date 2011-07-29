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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests for {@link SimpleMutableGroupAnnotation}.
 *
 * @author trejkaz
 */
public class SimpleMutableGroupAnnotationTest {

    MutableGroupAnnotation group = new SimpleMutableGroupAnnotation(0, 100, "");

    @Test
    public void testAdd_Consecutive() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        group.add(new SimpleMutableAnnotation(40, 20, new NullInterpreter(), ""));
    }

    @Test
    public void testAdd_ZeroLengthGroup() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        group.add(new SimpleMutableGroupAnnotation(40, 0, ""));
    }

    @Test
    public void testAdd_GroupAroundConsecutive() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        group.add(new SimpleMutableAnnotation(40, 20, new NullInterpreter(), ""));
        group.add(new SimpleMutableGroupAnnotation(20, 40, ""));
    }

    @Test
    public void testRemove() throws Exception {
        Annotation annotation = new SimpleMutableAnnotation(20, 20, new NullInterpreter(), "");
        group.add(annotation);
        group.remove(annotation);
    }

    @Test
    public void testRemove_NotPresent() throws Exception {
        Annotation annotation = new SimpleMutableAnnotation(20, 20, new NullInterpreter(), "");
        try {
            group.remove(annotation);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    @Test
    public void testRemove_GroupWithChildren() throws Exception {
        Annotation child1 = new SimpleMutableAnnotation(20, 20, new NullInterpreter(), "");
        group.add(child1);
        Annotation child2 = new SimpleMutableGroupAnnotation(40, 20, "");
        group.add(child2);
        GroupAnnotation innerGroup = new SimpleMutableGroupAnnotation(20, 40, "");
        group.add(innerGroup);

        group.remove(innerGroup);

        assertEquals(Arrays.asList(child1, child2), group.getAnnotations());
    }

    @Test
    public void testFindAnnotationAt_Found_HitAtBottom() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        assertNotNull(group.findAnnotationAt(20));
    }

    @Test
    public void testFindAnnotationAt_Found_HitAtTop() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        assertNotNull(group.findAnnotationAt(39));
    }

    @Test
    public void testFindAnnotationAt_NotFound_MissedByOneBelow() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        assertNull(group.findAnnotationAt(19));
    }

    @Test
    public void testFindAnnotationAt_NotFound_MissedByOneAbove() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        assertNull(group.findAnnotationAt(40));
    }

    @Test
    public void testFindDeepestGroupAnnotationAt_NotFound() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        assertNull(group.findDeepestGroupAnnotationAt(19));
    }

    @Test
    public void testFindDeepestGroupAnnotationAt_NotFound_ButFoundNonGroup() throws Exception {
        group.add(new SimpleMutableAnnotation(20, 20, new NullInterpreter(), ""));
        assertNull(group.findDeepestGroupAnnotationAt(20));
    }

    @Test
    public void testFindDeepestGroupAnnotationAt_Found_OneDeep() throws Exception {
        GroupAnnotation level1 = new SimpleMutableGroupAnnotation(20, 20, "level1");
        GroupAnnotation level2 = new SimpleMutableGroupAnnotation(25, 10, "level2");
        group.add(level1);
        group.add(level2);
        assertSame(level1, group.findDeepestGroupAnnotationAt(24));
    }

    @Test
    public void testFindDeepestGroupAnnotationAt_Found_TwoDeep() throws Exception {
        GroupAnnotation level1 = new SimpleMutableGroupAnnotation(20, 20, "level1");
        GroupAnnotation level2 = new SimpleMutableGroupAnnotation(25, 10, "level2");
        group.add(level1);
        group.add(level2);
        assertSame(level2, group.findDeepestGroupAnnotationAt(25));
    }
}
