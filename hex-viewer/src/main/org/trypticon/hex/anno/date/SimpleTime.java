/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

/**
 * Simple immutable time type.
 *
 * @author trejkaz
 */
public class SimpleTime implements Time {
    private final int hour;
    private final int minute;
    private final int second;
    private final int nanos;

    public SimpleTime(int hour, int minute, int second) {
        this(hour, minute, second, 0);
    }

    public SimpleTime(int hour, int minute, int second, int nanos) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nanos = nanos;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getNanos() {
        return nanos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Time))
        {
            return false;
        }

        Time that = (Time) o;

        return hour == that.getHour() && minute == that.getMinute() &&
                second == that.getSecond() && nanos == that.getNanos();
    }

    @Override
    public int hashCode() {
        int result = hour;
        result = 31 * result + minute;
        result = 31 * result + second;
        result = 31 * result + nanos;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%09d", hour, minute, second, nanos);
    }
}
