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

package org.trypticon.hex.gui;

import java.util.List;
import javax.swing.JMenu;

import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.interpreters.dates.DateInterpreterStorage;
import org.trypticon.hex.interpreters.nulls.NullInterpreterStorage;
import org.trypticon.hex.interpreters.primitives.PrimitiveInterpreterStorage;
import org.trypticon.hex.interpreters.strings.StringInterpreterStorage;

/**
 * With with the list of annotations which can be added by the user.
 *
 * @author trejkaz
 */
class AddAnnotationMenu extends JMenu {
    public AddAnnotationMenu() {
        super("Add Annotation");

        // TODO: This should go through the MasterInterpreterStorage once we have categories.

        add(buildPrimitivesMenu());
        add(buildDatesMenu());

        addSeparator();

        addAllToMenu(this, new StringInterpreterStorage().getInterpreterInfos());
        addAllToMenu(this, new NullInterpreterStorage().getInterpreterInfos());
    }

    private JMenu buildPrimitivesMenu() {
        JMenu menu = new JMenu("Primitives");
        addAllToMenu(menu, new PrimitiveInterpreterStorage().getInterpreterInfos());
        return menu;
    }

    private JMenu buildDatesMenu() {
        JMenu menu = new JMenu("Dates");
        addAllToMenu(menu, new DateInterpreterStorage().getInterpreterInfos());
        return menu;
    }

    private void addAllToMenu(JMenu menu, List<InterpreterInfo> infos) {
        for (InterpreterInfo info : infos) {
            menu.add(new AddAnnotationAction(info));
        }
    }

}
