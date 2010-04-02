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

package org.trypticon.hex.interpreters.strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trypticon.hex.interpreters.Interpreter;
import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.interpreters.InterpreterStorage;

/**
 * Storage support for string interpreters.
 *
 * @author trejkaz
 */
public class StringInterpreterStorage implements InterpreterStorage {
    public List<InterpreterInfo> getInterpreterInfos() {
        return Arrays.asList((InterpreterInfo) new StringInterpreterInfo());
    }

    public Map<String, Object> toMap(Interpreter interpreter) {
        if (interpreter instanceof StringInterpreter) {
            Map<String, Object> result = new HashMap<String, Object>(1);
            result.put("name", "string");
            result.put("charset", ((StringInterpreter) interpreter).getCharset());
            return result;
        } else {
            return null;
        }
    }

    public Interpreter fromMap(Map<String, Object> map) {
        String name = (String) map.get("name");
        if ("string".equals(name)) {
            String charset = (String) map.get("charset");
            return new StringInterpreter(charset);
        } else {
            return null;
        }
    }
}
