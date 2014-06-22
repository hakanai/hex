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

package org.trypticon.hex.gui.find;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link BinarySearcher}.
 *
 * @author trejkaz
 */
@RunWith(Parameterized.class)
@SuppressWarnings("HardCodedStringLiteral")
public class BinarySearcherTest {

    private final String needle;
    private final String haystack;
    private final long start;
    private final boolean wrapping;
    private final boolean backwards;
    private final Long expectedResult;

    public BinarySearcherTest(@NonNls String needle, @NonNls String haystack, long start,
                              boolean wrapping, boolean backwards, Integer expectedResult) {
        this.needle = needle;
        this.haystack = haystack;
        this.start = start;
        this.backwards = backwards;
        this.wrapping = wrapping;
        this.expectedResult = expectedResult == null ? null : Long.valueOf(expectedResult);
    }

    @Parameterized.Parameters()
    public static List<Object[]> parameters() {
        Object[][] data = {
            // No match.
            { "where", "not here", 0, false, false, null },

            // No match and the haystack smaller than the needle.
            { "pretty long", "pretty", 0, false, false, null },
            { "pretty long", "long", 0, false, false, null },

            // Single match at the start.
            { "ding", "dingoes", 0, false, false, 0 },
            { "ding", "dingoes", 1, false, false, null },

            // Single match in the middle.
            { "rave", "travesty", 0, false, false, 1 },
            { "rave", "travesty", 1, false, false, 1 },
            { "rave", "travesty", 2, false, false, null },

            // Single match at the end.
            { "ding", "reading", 0, false, false, 3 },
            { "ding", "reading", 3, false, false, 3 },
            { "ding", "reading", 4, false, false, null },

            // Single match at the end, only one letter.
            { "t", "biscuit", 0, false, false, 6 },

            // Starting past the end.
            { "t", "biscuit", 7, false, false, null },

            // Contains all characters but isn't a match.
            { "muter", "computer", 0, false, false, null },

            // Two matches, not overlapping
            { "ingmat", "stringmatchingmatching", 0, false, false, 3 },
            { "ingmat", "stringmatchingmatching", 3, false, false, 3 },
            { "ingmat", "stringmatchingmatching", 4, false, false, 11 },
            { "ingmat", "stringmatchingmatching", 11, false, false, 11 },
            { "ingmat", "stringmatchingmatching", 12, false, false, null },

            // Two matches, overlapping
            { "haha", "hahaha", 0, false, false, 0 },
            { "haha", "hahaha", 1, false, false, 2 },
            { "haha", "hahaha", 2, false, false, 2 },
            { "haha", "hahaha", 3, false, false, null },

            // Same string
            { "biscuits", "biscuits", 0, false, false, 0 },
            { "biscuits", "biscuits", 1, false, false, null },

            // Single match at the start, wrapping to find it.
            { "ding", "dingoes", 1, true, false, 0 },

            // Single match at the start, backwards to find it.
            { "ding", "dingoes", 6, false, true, 0 },

            // Single match at the end, wrapping backwards to find it.
            { "ding", "reading", 2, true, true, 3 },

        };
        return Arrays.asList(data);
    }

    @Test
    public void test() {
        Searcher searcher = new BinarySearcher(makeBinary(needle));
        Match result = searcher.find(makeBinary(haystack), start, new SearchParams(wrapping, backwards));
        if (expectedResult == null) {
            assertThat(result, is(nullValue()));
        } else {
            assertNotNull(result);
            assertThat(result.offset, is(expectedResult));
        }
    }

    private Binary makeBinary(@NonNls String str) {
        return BinaryFactory.wrap(str.getBytes(StandardCharsets.US_ASCII));
    }

}
