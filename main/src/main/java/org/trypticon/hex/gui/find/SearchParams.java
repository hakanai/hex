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

/**
 * Parameters for searching in the binary.
 *
 * @author trejkaz
 */
class SearchParams {

    private final boolean wrapping;
    private final boolean backwards;

    SearchParams(boolean wrapping, boolean backwards) {
        this.wrapping = wrapping;
        this.backwards = backwards;
    }

    public boolean isWrapping() {
        return wrapping;
    }

    public boolean isBackwards() {
        return backwards;
    }
}
