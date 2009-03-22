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

package org.trypticon.hex.anno.nulls;

import java.util.Map;
import java.util.HashMap;

import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.anno.InterpretorStorage;

/**
 * Interpretor storage for null values.
 *
 * @author trejkaz
 */
public class NullInterpretorStorage implements InterpretorStorage {
    public Map<String, Object> toMap(Interpretor interpretor) {
        if (interpretor.getClass() == NullInterpretor.class) {
            Map<String, Object> map = new HashMap<String, Object>(2);
            map.put("name", "null");
            map.put("length", ((NullInterpretor) interpretor).getLength());
            return map;
        } else {
            return null;
        }
    }

    public Interpretor fromMap(Map<String, Object> map) {
        if ("null".equals(map.get("name"))) {
            return new NullInterpretor(((Number) map.get("length")).intValue());
        } else {
            return null;
        }
    }
}
