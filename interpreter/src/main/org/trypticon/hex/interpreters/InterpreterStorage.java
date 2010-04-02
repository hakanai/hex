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

package org.trypticon.hex.interpreters;

import java.util.Map;

/**
 * Interface describing a collection of interpreters which can be used
 * to interpret values.  Provides support for converting these to and from
 * map form, for serialisation purposes.
 *
 * @author trejkaz
 */
public interface InterpreterStorage {
    /**
     * Converts an interpreter into a map.  The returned map generally
     * has a "name" property containing the interpreter name, which needs
     * to be unique across the whole system.
     *
     * XXX: Because it has to be unique, should the names be namespaced?
     *
     * @param interpreter the interpreter.
     * @return the map.  Returns {@code null} if the interpreter is not one
     *         known by this storage.
     */
    public abstract Map<String, Object> toMap(Interpreter interpreter);

    /**
     * Converts a map into an interpreter.
     *
     * @param map the map.
     * @return the interpreter.  Returns {@code null} if this storage does not
     *         know about an interpreter with the name specified in the map.
     */
    public abstract Interpreter fromMap(Map<String, Object> map);
}
