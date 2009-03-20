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

package org.trypticon.hex.anno;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.trypticon.hex.anno.primitive.PrimitiveInterpretorStorage;
import org.trypticon.hex.anno.nulls.NullInterpretorStorage;

/**
 * Storage routines for annotations.  Basically these just convert to and
 * from maps, which can then be converted into whatever format the caller
 * wants.
 *
 * @author trejkaz
 */
public class MasterInterpretorStorage implements InterpretorStorage {
    private List<InterpretorStorage> providers = new ArrayList<InterpretorStorage>(10);

    public MasterInterpretorStorage() {
        // TODO: Look up using Service Provider API.
        providers.add(new NullInterpretorStorage());
        providers.add(new PrimitiveInterpretorStorage());
    }

    public Map<String, Object> toMap(Interpretor interpretor) {
        for (InterpretorStorage provider : providers) {
            Map<String, Object> map = provider.toMap(interpretor);
            if (map != null) {
                return map;
            }
        }
        return null;
    }

    public Interpretor fromMap(Map<String, Object> map) {
        for (InterpretorStorage provider : providers) {
            Interpretor interpretor = provider.fromMap(map);
            if (interpretor != null) {
                return interpretor;
            }
        }
        return null;
    }
}
