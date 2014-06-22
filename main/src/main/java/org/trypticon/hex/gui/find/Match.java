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

import java.util.Objects;

import org.jetbrains.annotations.NonNls;

/**
 * Match result object.
 *
 * @author trejkaz
 */
class Match {
    final long offset;
    final long length;

    Match(long offset, long length) {
        this.offset = offset;
        this.length = length;
    }

    /**
     * Gets the end offset for the match, inclusive.
     *
     * @return the end offset for the match.
     */
    long endOffset() {
        return offset + length - 1;
    }

    @NonNls
    @Override
    public String toString() {
        return "offset " + offset + ", length " + length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Match)) {
            return false;
        }
        Match that = (Match) obj;
        return offset == that.offset && length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, length);
    }
}
