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

package org.trypticon.hex.interpreters.primitives;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterStorage;

/**
 * Storage support for primitive interpreters.
 *
 * @author trejkaz
 */
public class PrimitiveInterpreterStorage implements InterpreterStorage {
    private final Map<Class<? extends Interpreter>, String> classToName =
            new HashMap<Class<? extends Interpreter>, String>(10);
    private final Map<String, Class<? extends Interpreter>> nameToClass =
            new HashMap<String, Class<? extends Interpreter>>(10);

    public PrimitiveInterpreterStorage() {
        register("uint8", UByteInterpreter.class);
        register("uint16_be", UShortInterpreterBE.class);
        register("uint16_le", UShortInterpreterLE.class);
        register("uint32_be", UIntInterpreterBE.class);
        register("uint32_le", UIntInterpreterLE.class);
        register("uint64_be", ULongInterpreterBE.class);
        register("uint64_le", ULongInterpreterLE.class);
        register("float32_be", FloatInterpreterBE.class);
        register("float32_le", FloatInterpreterLE.class);
        register("float64_be", DoubleInterpreterBE.class);
        register("float64_le", DoubleInterpreterLE.class);
    }

    private void register(String name, Class<? extends Interpreter> klass) {
        classToName.put(klass, name);
        nameToClass.put(name, klass);
    }

    public List<InterpreterInfo> getInterpreterInfos() {
        // TODO: Interpreter info should be structured to allow categorising them as well, for menus.

        return Arrays.asList(new UByteInterpreterInfo(),
                             new UShortInterpreterBEInfo(),
                             new UShortInterpreterLEInfo(),
                             new UIntInterpreterBEInfo(),
                             new UIntInterpreterLEInfo(),
                             new ULongInterpreterBEInfo(),
                             new ULongInterpreterLEInfo(),
                             new FloatInterpreterBEInfo(),
                             new FloatInterpreterLEInfo(),
                             new DoubleInterpreterBEInfo(),
                             new DoubleInterpreterLEInfo());
    }

    public Map<String, Object> toMap(Interpreter interpreter) {
        String name = classToName.get(interpreter.getClass());
        if (name != null) {
            Map<String, Object> result = new HashMap<String, Object>(1);
            result.put("name", name);
            return result;
        } else {
            return null;
        }
    }

    public Interpreter fromMap(Map<String, Object> map) {
        String name = (String) map.get("name");
        Class<? extends Interpreter> klass = nameToClass.get(name);
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
