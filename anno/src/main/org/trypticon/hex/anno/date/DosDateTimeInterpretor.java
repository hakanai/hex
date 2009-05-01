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
import org.trypticon.hex.anno.AbstractFixedLengthInterpretor;
import org.trypticon.hex.binary.Binary;

/**
 * Interprets a pair of DOS date and time.
 *
 * @author trejkaz
 */
public class DosDateTimeInterpretor extends AbstractFixedLengthInterpretor<DateTime> {
    private final Interpretor<Date> dateInterp = new DosDateInterpretor();
    private final Interpretor<Time> timeInterp = new DosTimeInterpretor();

    public DosDateTimeInterpretor() {
        super(DateTime.class, 4);
    }

    public DateTime interpret(Binary binary, long position) {
        return new SimpleDateTime(dateInterp.interpret(binary, position + 2, 2),
                                  timeInterp.interpret(binary, position, 2));
    }
}
