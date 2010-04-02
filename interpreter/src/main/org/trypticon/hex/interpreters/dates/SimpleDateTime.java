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
 * Simple immutable date-time type.
 *
 * @author trejkaz
 */
public class SimpleDateTime implements DateTime {
    private final Date date;
    private final Time time;

    public SimpleDateTime(Date date, Time time) {
        this.date = date;
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public long getMillis() {
        throw new UnsupportedOperationException("TODO: Haven't decided to keep this method yet");
    }

    public int length() {
        return date.length() + time.length();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof DateTime))
        {
            return false;
        }

        DateTime that = (DateTime) o;
        return date.equals(that.getDate()) && time.equals(that.getTime());
    }

    @Override
    public int hashCode() {
        return 31 * date.hashCode() + time.hashCode();
    }

    @Override
    public String toString() {
        return date.toString() + " " + time.toString();
    }
}
