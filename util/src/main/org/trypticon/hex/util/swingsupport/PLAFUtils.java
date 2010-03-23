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

package org.trypticon.hex.util.swingsupport;

import javax.swing.UIManager;

/**
 * Look and Feel utilities.
 *
 * @author trejkaz
 */
public class PLAFUtils {
    /**
     * Tests if the look and feel is Quaqua, which we use on Mac to get better look and feel for Mac.
     *
     * @return {@code true} if using Quaqua, otherwise {@code false}.
     */
    public static boolean isQuaqua() {
        return "ch.randelshofer.quaqua.QuaquaLookAndFeel".equals(UIManager.getLookAndFeel().getClass().getName());
    }
}
