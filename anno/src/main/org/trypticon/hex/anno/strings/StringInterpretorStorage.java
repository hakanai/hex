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

package org.trypticon.hex.anno.strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.anno.InterpretorInfo;
import org.trypticon.hex.anno.InterpretorStorage;

/**
 * Storage support for string interpretors.
 *
 * @author trejkaz
 */
public class StringInterpretorStorage implements InterpretorStorage {
    public List<InterpretorInfo> getInterpretorInfos() {
        return Arrays.asList((InterpretorInfo) new StringInterpretorInfo());
    }

    public Map<String, Object> toMap(Interpretor interpretor) {
        if (interpretor instanceof StringInterpretor) {
            Map<String, Object> result = new HashMap<String, Object>(1);
            result.put("name", "string");
            result.put("charset", ((StringInterpretor) interpretor).getCharset());
            return result;
        } else {
            return null;
        }
    }

    public Interpretor fromMap(Map<String, Object> map) {
        String name = (String) map.get("name");
        if ("string".equals(name)) {
            String charset = (String) map.get("charset");
            return new StringInterpretor(charset);
        } else {
            return null;
        }
    }
}
