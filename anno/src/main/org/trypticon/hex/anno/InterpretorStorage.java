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

package org.trypticon.hex.anno;

import java.util.Map;

/**
 * Interface describing a collection of interpretors which can be used
 * to interpret values.  Provides support for converting these to and from
 * map form, for serialisation purposes.
 *
 * @author trejkaz
 */
public interface InterpretorStorage {
    /**
     * Converts an interpretor into a map.  The returned map generally
     * has a "name" property containing the interpretor name, which needs
     * to be unique across the whole system.
     *
     * XXX: Because it has to be unique, should the names be namespaced?
     *
     * @param interpretor the interpretor.
     * @return the map.  Returns {@code null} if the interpretor is not one
     *         known by this storage.
     */
    public abstract Map<String, Object> toMap(Interpretor interpretor);

    /**
     * Converts a map into an interpretor.
     *
     * @param map the map.
     * @return the interpretor.  Returns {@code null} if this storage does not
     *         know about an interpretor with the name specified in the map.
     */
    public abstract Interpretor fromMap(Map<String, Object> map);
}
