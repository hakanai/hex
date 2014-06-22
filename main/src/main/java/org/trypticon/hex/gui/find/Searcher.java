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

import org.jetbrains.annotations.Nullable;

import org.trypticon.hex.binary.Binary;

/**
 * Abstraction of how to search the binary.
 *
 * @author trejkaz
 */
interface Searcher {

    /**
     * Searches the binary.
     *
     * @param haystack the binary being searched.
     * @param startPosition the position to start searching from.
     * @param params the search parameters.
     * @return the found position, or {@code null} if there is no match.
     */
    @Nullable
    Match find(Binary haystack, long startPosition, SearchParams params);
}
