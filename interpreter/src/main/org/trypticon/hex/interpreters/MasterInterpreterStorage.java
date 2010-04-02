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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.trypticon.hex.interpreters.nulls.NullInterpreterStorage;
import org.trypticon.hex.interpreters.primitives.PrimitiveInterpreterStorage;
import org.trypticon.hex.interpreters.strings.StringInterpreterStorage;

/**
 * Storage routines for annotations.  Basically these just convert to and
 * from maps, which can then be converted into whatever format the caller
 * wants.
 *
 * @author trejkaz
 */
public class MasterInterpreterStorage implements InterpreterStorage {
    private List<InterpreterStorage> providers = new ArrayList<InterpreterStorage>(10);

    public MasterInterpreterStorage() {
        // TODO: Look up using Service Provider API.
        providers.add(new NullInterpreterStorage());
        providers.add(new PrimitiveInterpreterStorage());
        providers.add(new StringInterpreterStorage());
    }

    public Map<String, Object> toMap(Interpreter interpreter) {
        for (InterpreterStorage provider : providers) {
            Map<String, Object> map = provider.toMap(interpreter);
            if (map != null) {
                return map;
            }
        }
        return null;
    }

    public Interpreter fromMap(Map<String, Object> map) {
        for (InterpreterStorage provider : providers) {
            Interpreter interpreter = provider.fromMap(map);
            if (interpreter != null) {
                return interpreter;
            }
        }
        return null;
    }
}
