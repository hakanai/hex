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

package org.trypticon.hex.anno.date;

import org.trypticon.hex.anno.Value;

/**
 * A value carrying a time (and not a date.)
 *
 * @author trejkaz
 */
public interface Time extends Value {
    /**
     * Gets the hour.
     *
     * @return the hour.
     */
    int getHour();

    /**
     * Gets the minute.
     *
     * @return the minute.
     */
    int getMinute();

    /**
     * Gets the second.
     *
     * @return the second.
     */
    int getSecond();

    /**
     * Gets the nanosecond (0 to 999,999,999)
     *
     * @return the nanosecond.
     */
    int getNanos();
}
