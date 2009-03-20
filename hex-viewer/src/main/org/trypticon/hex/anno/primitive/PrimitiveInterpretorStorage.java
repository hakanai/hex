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

import java.util.Map;
import java.util.HashMap;

import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.anno.InterpretorStorage;

/**
 * Storage support for primitive interpretors.
 *
 * @author trejkaz
 */
public class PrimitiveInterpretorStorage implements InterpretorStorage {
    private Map<Class<? extends Interpretor>, String> classToName =
            new HashMap<Class<? extends Interpretor>, String>(10);
    private Map<String, Class<? extends Interpretor>> nameToClass =
            new HashMap<String, Class<? extends Interpretor>>(10);

    public PrimitiveInterpretorStorage() {
        // TODO: More types.
        register("uint2le", UShortInterpretorLE.class);
        register("uint2be", UShortInterpretorBE.class);
        register("uint4le", UIntInterpretorLE.class);
        register("uint4be", UIntInterpretorBE.class);
    }

    private void register(String name, Class<? extends Interpretor> klass) {
        classToName.put(klass, name);
        nameToClass.put(name, klass);
    }

    // TODO: I think it would be nicer to separate the name from the map and have the name
    //       as a string and the remaining options as a map.  I'm still looking for a good
    //       way to do this.

    public Map<String, Object> toMap(Interpretor interpretor) {
        String name = classToName.get(interpretor.getClass());
        if (name != null) {
            Map<String, Object> result = new HashMap<String, Object>(1);
            result.put("name", name);
            return result;
        } else {
            return null;
        }
    }

    public Interpretor fromMap(Map<String, Object> map) {
        String name = (String) map.get("name");
        Class<? extends Interpretor> klass = nameToClass.get(name);
        if (klass != null) {
            try {
                return klass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalStateException("Constructor should have been no-op", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Constructor should have been accessible", e);
            }
        } else {
            return null;
        }
    }
}
