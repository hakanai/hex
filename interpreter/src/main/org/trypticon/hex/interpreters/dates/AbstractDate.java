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

package org.trypticon.hex.interpreters.dates;

/**
 * Helper abstract class for defining new types of date.
 *
 * @author trejkaz
 */
public abstract class AbstractDate implements Date {
    @Override
    public boolean equals(Object o) {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Date))
        {
            return false;
        }

        Date that = (Date) o;

        return getDay() == that.getDay() && getMonth() == that.getMonth() && getYear() == that.getYear();
    }

    @Override
    public int hashCode() {
        int result = getYear();
        result = 31 * result + getMonth();
        result = 31 * result + getDay();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%04d/%02d/%02d", getYear(), getMonth(), getDay());
    }
}
