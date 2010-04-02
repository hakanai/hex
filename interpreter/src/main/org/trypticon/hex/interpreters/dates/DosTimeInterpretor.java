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

import org.trypticon.hex.interpreters.AbstractFixedLengthInterpretor;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.interpreters.primitives.BitField;
import org.trypticon.hex.interpreters.primitives.LittleEndian;

/**
 * Interpretor for MS-DOS time values, which are used in a number of other formats.
 *
 * @author trejkaz
 */
public class DosTimeInterpretor extends AbstractFixedLengthInterpretor<Time> {
    private static final BitField second = BitField.lowest(5);
    private static final BitField minute = second.next(6);
    private static final BitField hour = minute.next(5);
    private static final int SECOND_RESOLUTION = 2;

    public DosTimeInterpretor() {
        super(Time.class, 2);
    }

    public Time interpret(Binary binary, long position) {
        return new DosTime(LittleEndian.getShort(binary, position));
    }

    private static class DosTime extends AbstractTime {
        private final short value;
        private DosTime(short value) {
            this.value = value;
        }

        public int getHour() {
            return hour.evaluate(value);
        }

        public int getMinute() {
            return minute.evaluate(value);
        }

        public int getSecond() {
            return second.evaluate(value) * SECOND_RESOLUTION;
        }

        public int length() {
            return 2;
        }
    }
}
