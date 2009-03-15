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
 * Interpretor for MS-DOS time values, which are used in a number of other formats.
 *
 * @author trejkaz
 */
public class DosTimeInterpretor implements Interpretor<Time> {
    private static final BitField second = BitField.lowest(5);
    private static final BitField minute = second.next(6);
    private static final BitField hour = minute.next(5);
    private static final int SECOND_RESOLUTION = 2;

    public Class<Time> getType() {
        return Time.class;
    }

    public Time interpret(Binary binary, long position, int length) {
        int value = LittleEndian.getUShort(binary, position);

        return new SimpleTime(hour.evaluate(value), minute.evaluate(value),
                              second.evaluate(value) * SECOND_RESOLUTION);
    }
}
