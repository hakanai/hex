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

import java.nio.charset.Charset;

import org.jetbrains.annotations.NonNls;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link TextSearcher}.
 *
 * @author trejkaz
 */
@SuppressWarnings("HardCodedStringLiteral")
public class TextSearcherTest {

    public static Object[][] parameters() {
        return new Object[][] {
            // No match.
            { "where", "US-ASCII", "not here", 0, false, false, null },

            // No match and the haystack smaller than the needle.
            { "pretty long", "US-ASCII", "pretty", 0, false, false, null },
            { "pretty long", "US-ASCII", "long", 0, false, false, null },

            // Single match at the start.
            { "ding", "US-ASCII", "dingoes", 0, false, false, new Match(0, 4) },
            { "ding", "US-ASCII", "dingoes", 1, false, false, null },

            // Single match in the middle.
            { "rave", "US-ASCII", "travesty", 0, false, false, new Match(1, 4) },
            { "rave", "US-ASCII", "travesty", 1, false, false, new Match(1, 4) },
            { "rave", "US-ASCII", "travesty", 2, false, false, null },

            // Single match at the end.
            { "ding", "US-ASCII", "reading", 0, false, false, new Match(3, 4) },
            { "ding", "US-ASCII", "reading", 3, false, false, new Match(3, 4) },
            { "ding", "US-ASCII", "reading", 4, false, false, null },

            // Single match at the end, only one letter.
            { "t", "US-ASCII", "biscuit", 0, false, false, new Match(6, 1) },

            // Starting past the end.
            { "t", "US-ASCII", "biscuit", 7, false, false, null },

            // Contains all characters but isn't a match.
            { "muter", "US-ASCII", "computer", 0, false, false, null },

            // Two matches, not overlapping
            { "ingmat", "US-ASCII", "stringmatchingmatching", 0, false, false, new Match(3, 6) },
            { "ingmat", "US-ASCII", "stringmatchingmatching", 3, false, false, new Match(3, 6) },
            { "ingmat", "US-ASCII", "stringmatchingmatching", 4, false, false, new Match(11, 6) },
            { "ingmat", "US-ASCII", "stringmatchingmatching", 11, false, false, new Match(11, 6) },
            { "ingmat", "US-ASCII", "stringmatchingmatching", 12, false, false, null },

            // Two matches, overlapping
            { "haha", "US-ASCII", "hahaha", 0, false, false, new Match(0, 4) },
            { "haha", "US-ASCII", "hahaha", 1, false, false, new Match(2, 4) },
            { "haha", "US-ASCII", "hahaha", 2, false, false, new Match(2, 4) },
            { "haha", "US-ASCII", "hahaha", 3, false, false, null },

            // Same string
            { "biscuits", "US-ASCII", "biscuits", 0, false, false, new Match(0, 8) },
            { "biscuits", "US-ASCII", "biscuits", 1, false, false, null },

            // Single match at the start, wrapping to find it.
            { "ding", "US-ASCII", "dingoes", 1, true, false, new Match(0, 4) },

            // Single match at the start, backwards to find it.
            { "ding", "US-ASCII", "dingoes", 6, false, true, new Match(0, 4) },

            // Single match at the end, wrapping backwards to find it.
            { "ding", "US-ASCII", "reading", 2, true, true, new Match(3, 4) },

            // Normalisation forms
            { "caf\u00E9", "UTF-8", "cafe\u0301", 0, false, false, new Match(0, 6) },
            { "cafe\u0301", "UTF-8", "caf\u00E9", 0, false, false, new Match(0, 5) },

            // Binary junk before the match
            { "cake", "UTF-8", "\u0001\u0000\u0004cake", 0, false, false, new Match(3, 4) },
        };
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void test(@NonNls String needle, @NonNls String charsetName, @NonNls String haystack,
                     long start, boolean wrapping, boolean backwards, Match expectedResult) {
        Charset charset = Charset.forName(charsetName);
        Searcher searcher = new TextSearcher(needle, charset);
        Binary binary = BinaryFactory.wrap(haystack.getBytes(charset));
        Match result = searcher.find(binary, start, new SearchParams(wrapping, backwards));
        assertThat(result, is(equalTo(expectedResult)));
    }
}
