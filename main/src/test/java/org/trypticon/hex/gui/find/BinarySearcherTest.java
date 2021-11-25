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

import org.jetbrains.annotations.NonNls;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for {@link BinarySearcher}.
 *
 * @author trejkaz
 */
@SuppressWarnings("HardCodedStringLiteral")
public class BinarySearcherTest {

    public static Object[][] parameters() {
        return new Object[][] {
            // No match.
            { "where", "not here", 0L, false, false, null },

            // No match and the haystack smaller than the needle.
            { "pretty long", "pretty", 0L, false, false, null },
            { "pretty long", "long", 0L, false, false, null },

            // Single match at the start.
            { "ding", "dingoes", 0L, false, false, 0L },
            { "ding", "dingoes", 1L, false, false, null },

            // Single match in the middle.
            { "rave", "travesty", 0L, false, false, 1L },
            { "rave", "travesty", 1L, false, false, 1L },
            { "rave", "travesty", 2L, false, false, null },

            // Single match at the end.
            { "ding", "reading", 0L, false, false, 3L },
            { "ding", "reading", 3L, false, false, 3L },
            { "ding", "reading", 4L, false, false, null },

            // Single match at the end, only one letter.
            { "t", "biscuit", 0L, false, false, 6L },

            // Starting past the end.
            { "t", "biscuit", 7L, false, false, null },

            // Contains all characters but isn't a match.
            { "muter", "computer", 0L, false, false, null },

            // Two matches, not overlapping
            { "ingmat", "stringmatchingmatching", 0L, false, false, 3L },
            { "ingmat", "stringmatchingmatching", 3L, false, false, 3L },
            { "ingmat", "stringmatchingmatching", 4L, false, false, 11L },
            { "ingmat", "stringmatchingmatching", 11L, false, false, 11L },
            { "ingmat", "stringmatchingmatching", 12L, false, false, null },

            // Two matches, overlapping
            { "haha", "hahaha", 0L, false, false, 0L },
            { "haha", "hahaha", 1L, false, false, 2L },
            { "haha", "hahaha", 2L, false, false, 2L },
            { "haha", "hahaha", 3L, false, false, null },

            // Same string
            { "biscuits", "biscuits", 0L, false, false, 0L },
            { "biscuits", "biscuits", 1L, false, false, null },

            // Single match at the start, wrapping to find it.
            { "ding", "dingoes", 1L, true, false, 0L },

            // Single match at the start, backwards to find it.
            { "ding", "dingoes", 6L, false, true, 0L },

            // Single match at the end, wrapping backwards to find it.
            { "ding", "reading", 2L, true, true, 3L },
        };
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void test(@NonNls String needle, @NonNls String haystack, long start,
                     boolean wrapping, boolean backwards, Long expectedResult) {
        Searcher searcher = new BinarySearcher(makeBinary(needle));
        Match result = searcher.find(makeBinary(haystack), start, new SearchParams(wrapping, backwards));
        if (expectedResult == null) {
            assertThat(result, is(nullValue()));
        } else {
            assertThat(result, is(notNullValue()));
            assertThat(result.offset, is(expectedResult));
        }
    }

    private Binary makeBinary(@NonNls String str) {
        return BinaryFactory.wrap(str.getBytes(StandardCharsets.US_ASCII));
    }

}
