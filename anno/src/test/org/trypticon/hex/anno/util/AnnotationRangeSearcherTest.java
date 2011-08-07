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
import org.trypticon.hex.interpreters.nulls.NullInterpreter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.trypticon.hex.anno.util.AnnotationRangeSearchHit.Relation.*;

/**
 * Tests for {@link AnnotationRangeSearcher}.
 */
public class AnnotationRangeSearcherTest {
    AnnotationRangeSearcher searcher = new AnnotationRangeSearcher();
    List<Annotation> list;
    List<AnnotationRangeSearchHit> expected;

    @Test
    public void testOutside() {
        makeList(12, 4);
        expect();
        test(16, 4);
    }

    @Test
    public void testSameRange() {
        makeList(12, 4);
        expect(0, SAME_RANGE);
        test(12, 4);
    }

    @Test
    public void testContainedWithinWithExcessOnLeft() {
        makeList(12, 4);
        expect(0, CONTAINED_WITHIN);
        test(11, 5);
    }

    @Test
    public void testContainedWithinWithExcessOnRight() {
        makeList(12, 4);
        expect(0, CONTAINED_WITHIN);
        test(12, 5);
    }

    @Test
    public void testContainedWithinWithExcessOnBothSides() {
        makeList(12, 4);
        expect(0, CONTAINED_WITHIN);
        test(11, 6);
    }

    @Test
    public void testContainedWithinWithExcessAndAnnotationsOutsideRange() {
        makeList(6, 4, 12, 4, 18, 4);
        expect(1, CONTAINED_WITHIN);
        test(11, 6);
    }

    @Test
    public void testSurroundsWithExcessOnRight() {
        makeList(12, 6);
        expect(0, SURROUNDING);
        test(12, 4);
    }

    @Test
    public void testSurroundsWithExcessOnLeft() {
        makeList(12, 6);
        expect(0, SURROUNDING);
        test(14, 4);
    }

    @Test
    public void testSurroundsWithExcessOnBothSides() {
        makeList(12, 6);
        expect(0, SURROUNDING);
        test(13, 4);
    }

    @Test
    public void testIntersectingStart() {
        makeList(4, 4);
        expect(0, INTERSECTING_START);
        test(6, 4);
    }

    @Test
    public void testIntersectingEnd() {
        makeList(4, 4);
        expect(0, INTERSECTING_END);
        test(2, 4);
    }

    @Test
    public void testIntersectingBothEndsWithOneInTheMiddle() {
        makeList(4, 4, 8, 4, 12, 4);
        expect(0, INTERSECTING_START, 1, CONTAINED_WITHIN, 2, INTERSECTING_END);
        test(6, 8);
    }

    private void makeList(int... positionsAndLengths) {
        list = new ArrayList<Annotation>(positionsAndLengths.length / 2);
        for (int i = 0; i < positionsAndLengths.length; i += 2) {
            list.add(new SimpleMutableAnnotation(positionsAndLengths[i], positionsAndLengths[i + 1], new NullInterpreter(), null));
        }
    }

    private void expect(Object... indicesAndRelations) {
        expected = new ArrayList<AnnotationRangeSearchHit>(indicesAndRelations.length / 2);
        for (int i = 0; i < indicesAndRelations.length; i += 2) {
            int index = (Integer) indicesAndRelations[i];
            AnnotationRangeSearchHit.Relation relation = (AnnotationRangeSearchHit.Relation) indicesAndRelations[i + 1];
            expected.add(new AnnotationRangeSearchHit(list.get(index), relation));
        }
    }

    private void test(int position, int length) {
        assertEquals("Wrong result", expected, searcher.findAllInRange(list, new SimpleMutableAnnotation(position, length, new NullInterpreter(), null)));
    }
}
