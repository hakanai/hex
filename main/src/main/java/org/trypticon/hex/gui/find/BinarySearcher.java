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

import org.trypticon.hex.binary.Binary;

/**
 * Searcher which searches for binary.
 *
 * @author trejkaz
 */
class BinarySearcher implements Searcher {
    private final Binary needle;
    private final int[] table;

    BinarySearcher(Binary needle) {
        this.needle = needle;
        table = precomputeTable(needle);
    }

    @Override
    public Match find(Binary haystack, long startPosition, SearchParams params) {
        Binary needle = this.needle;
        if (params.isBackwards()) {
            needle = new ReversedBinary(needle);
            haystack = new ReversedBinary(haystack);
            startPosition = haystack.length() - 1 - startPosition;
        }

        long stopPosition = haystack.length() - needle.length() + 1;
        Match found = kmpSearch(needle, haystack, startPosition, stopPosition);

        if (found == null && params.isWrapping()) {
            found = kmpSearch(needle, haystack, 0, startPosition);
        }

        if (found != null && params.isBackwards()) {
            found = new Match(haystack.length() - needle.length() - found.offset, needle.length());
        }

        return found;
    }

    /**
     * Knuth-Morris-Pratt algorithm for finding a single binary inside a larger binary.
     *
     * @param needle the needle.
     * @param haystack the haystack.
     * @param startPosition the first index to check.
     * @param stopPosition the index to stop at.
     * @return the next offset found, or {@code null} if no match is found by the end.
     */
    private Match kmpSearch(Binary needle, Binary haystack, long startPosition, long stopPosition) {
        int needleIndex = 0;
        int needleLength = (int) needle.length();
        long haystackIndex = startPosition;
        while (haystackIndex < stopPosition) {
            if (needle.read(needleIndex) == haystack.read(haystackIndex + needleIndex)) {
                if (needleIndex == needleLength - 1) {
                    return new Match(haystackIndex, needleLength);
                }
                needleIndex ++;
            } else {
                if (table[needleIndex] > -1) {
                    needleIndex = table[needleIndex];
                    haystackIndex += needleIndex - table[needleIndex];
                } else {
                    needleIndex = 0;
                    haystackIndex ++;
                }
            }
        }
        return null;
    }

    /**
     * Precomputes the kmpNext lookup table for the given needle.
     *
     * @param needle the needle.
     * @return the lookup table.
     */
    private int[] precomputeTable(Binary needle) {
        int needleLength = (int) needle.length();
        int[] kmpNext = new int[needleLength];
        int i = 0;
        int j = kmpNext[0] = -1;
        while (i < needleLength) {
            while (j > -1 && needle.read(i) != needle.read(j)) {
                j = kmpNext[j];
            }
            i++;
            j++;
            if (i >= needleLength) { // this branch isn't in copies
                break;
            }
            if (needle.read(i) == needle.read(j)) {
                kmpNext[i] = kmpNext[j];
            } else {
                kmpNext[i] = j;
            }
        }
        return kmpNext;
    }
}
