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

import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.anno.primitive.LittleEndian;
import org.trypticon.hex.anno.primitive.BitField;
import org.trypticon.binary.Binary;

/**
 * Interpretor for MS-DOS date values, which are used in a number of other formats.
 *
 * @author trejkaz
 */
public class DosDateInterpretor implements Interpretor<Date> {
    private static final BitField day = BitField.lowest(5);
    private static final BitField month = day.next(4);
    private static final BitField year = month.next(7);
    private static final int YEAR_OFFSET = 1980;

    public Class<Date> getType() {
        return Date.class;
    }

    public Date interpret(Binary binary, long position, int length) {
        return new DosDate(LittleEndian.getShort(binary, position));
    }

    private static class DosDate extends AbstractDate {
        private final short value;
        private DosDate(short value) {
            this.value = value;
        }

        public int getYear() {
            return year.evaluate(value) + YEAR_OFFSET;
        }

        public int getMonth() {
            return month.evaluate(value);
        }

        public int getDay() {
            return day.evaluate(value);
        }

        public int length() {
            return 2;
        }
    }
}
