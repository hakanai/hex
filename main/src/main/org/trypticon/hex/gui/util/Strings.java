/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
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

package org.trypticon.hex.gui.util;

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.StringSearch;

/**
 * String utility methods.
 *
 * @author trejkaz
 */
public class Strings {
    public static String[] splitOnWhitespace(String string) {
        return string.split("\\s+");
    }

    public static boolean containsIgnoreCase(String haystack, String needle) {
        return indexOfIgnoreCase(haystack, needle) >= 0;
    }

    public static int indexOfIgnoreCase(String haystack, String needle) {
        StringSearch stringSearch = new StringSearch(needle, haystack);
        stringSearch.getCollator().setStrength(Collator.PRIMARY);
        return stringSearch.first();
    }
}
