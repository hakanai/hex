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

package org.trypticon.hex;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link HexUtils}.
 *
 * @author trejkaz
 */
public class TestHexUtils {
    @Test
    public void testToHex() {
        assertEquals("Wrong hex", "0A", HexUtils.toHex((byte) 0x0A));
        assertEquals("Wrong hex", "CA", HexUtils.toHex((byte) 0xCA));
    }

    @Test
    public void testToAscii() {
        assertEquals("Should be replaced", ".", HexUtils.toAscii((byte) 0x00));
        assertEquals("Should be replaced", ".", HexUtils.toAscii((byte) 0x1F));

        assertEquals("Wrong ASCII", " ", HexUtils.toAscii((byte) 0x20));
        assertEquals("Wrong ASCII", "U", HexUtils.toAscii((byte) 0x55));

        assertEquals("Should be replaced", ".", HexUtils.toAscii((byte) 0xCA));
        assertEquals("Should be replaced", ".", HexUtils.toAscii((byte) 0xFE));
        assertEquals("Should be replaced", ".", HexUtils.toAscii((byte) 0xBA));
        assertEquals("Should be replaced", ".", HexUtils.toAscii((byte) 0xBE));
    }
}
