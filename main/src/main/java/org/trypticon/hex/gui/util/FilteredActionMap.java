/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014  Trejkaz, Hex Project
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

package org.trypticon.hex.gui.util;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.Action;
import javax.swing.ActionMap;

/**
 * An action map which pretends elements are missing.
 *
 * @author trejkaz
 */
public class FilteredActionMap extends ActionMap {
    private final Set<Object> blackList;

    public FilteredActionMap(ActionMap realActionMap, Object blackList) {
        setParent(realActionMap);
        this.blackList = new LinkedHashSet<>();
        this.blackList.add(blackList);
    }

    public FilteredActionMap(ActionMap realActionMap, Set<?> blackList) {
        setParent(realActionMap);
        this.blackList = new LinkedHashSet<>(blackList);
    }

    @Override
    public Action get(Object key) {
        if (blackList.contains(key)) {
            return null;
        }
        return super.get(key);
    }
}
