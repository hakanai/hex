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

package org.trypticon.hex.anno.util;

import org.junit.Test;
import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.interpreters.nulls.NullInterpretor;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.trypticon.hex.anno.util.Annotations.*;

/**
 * Tests for {@link Annotations}.
 *
 * @author trejkaz
 */
public class AnnotationsTest {

    @Test
    public void testContainsForOverlapOfOnePosition() {
        assertFalse(contains(a(0, 10), a(9, 10)));
    }

    @Test
    public void testContainsForTouchingRegions() {
        assertFalse(contains(a(0, 10), a(10, 10)));
    }

    @Test
    public void testContainsForFirstContainedInSecond() {
        assertFalse(contains(a(0, 5), a(0, 10)));
    }

    @Test
    public void testContainsForSecondContainedInFirst() {
        assertTrue(contains(a(0, 10), a(0, 5)));
    }

    @Test
    public void testContainsForSameRegion() {
        assertTrue(contains(a(0, 10), a(0, 10)));
    }

    @Test
    public void testOverlapForOverlapOfOnePosition() {
        assertTrue(overlap(a(0, 10), a(9, 10)));
    }

    @Test
    public void testOverlapForTouchingRegions() {
        assertFalse(overlap(a(0, 10), a(10, 10)));
    }

    @Test
    public void testOverlapForFirstContainedInSecond() {
        assertTrue(overlap(a(0, 5), a(0, 10)));
    }

    @Test
    public void testOverlapForSecondContainedInFirst() {
        assertTrue(overlap(a(0, 10), a(0, 5)));
    }

    @Test
    public void testOverlapForSameRegion() {
        assertTrue(overlap(a(0, 10), a(0, 10)));
    }

    private static Annotation a(int start, int length) {
        return new SimpleMutableAnnotation(start, length, new NullInterpretor(), null);
    }
}
