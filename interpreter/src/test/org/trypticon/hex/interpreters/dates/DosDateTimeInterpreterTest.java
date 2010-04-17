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

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.binary.BinaryFactory;

/**
 * Tests for {@link DosDateTimeInterpreter}.
 *
 * @author trejkaz
 */
public class DosDateTimeInterpreterTest {
    @Test
    public void testInterpret() {
        Interpreter<DateTime> interpreter = new DosDateTimeInterpreter();

        byte[] data = { 0x67, 0x64, (byte) 0xAC, (byte) 0x2E };

        assertDateTimeEquals("Wrong date value", 2003, 5, 12, 12, 35, 14,
                             interpreter.interpret(BinaryFactory.wrap(data), 0, 4));

    }

    private static void assertDateTimeEquals(String message, int year, int month, int day,
                                             int hour, int minute, int second,
                                             DateTime value) {

        assertEquals(message + " (year)", year, value.getDate().getYear());
        assertEquals(message + " (month)", month, value.getDate().getMonth());
        assertEquals(message + " (day)", day, value.getDate().getDay());
        assertEquals(message + " (hour)", hour, value.getTime().getHour());
        assertEquals(message + " (minute)", minute, value.getTime().getMinute());
        assertEquals(message + " (second)", second, value.getTime().getSecond());
    }
}
