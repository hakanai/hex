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

package org.trypticon.hex.formats;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.binary.Binary;

/**
 * Knows how to create annotations which match a certain structure.
 *
 * @author trejkaz
 */
public interface Structure {
    /**
     * Gets the name of the structure.
     *
     * @return the name of the structure.
     */
    String getName();

    /**
     * Creates an annotation representing the structure.  May be a group annotation containing further annotations.
     *
     * @param binary the binary to annotate.
     * @param position the position to start from.
     * @return the created annotation.
     */
    Annotation drop(Binary binary, long position);
}
