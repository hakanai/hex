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

package org.trypticon.hex.anno.primitive;

import org.trypticon.hex.anno.Interpretor;

/**
 * Convenience class holding a bunch of instances to primitive interpretors.
 *
 * @author trejkaz
 */
public class PrimitiveInterpretors {
    // TODO: Many more.  Putting in the minimum to test I/O code for now.

    public static final Interpretor<UInt> UINT32_LE = new UIntInterpretorLE();
}
