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

package org.trypticon.hex.gui.scripting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;

import org.trypticon.hex.gui.util.MenuAdapter;

/**
 * A script menu.
 *
 * @author trejkaz
 */
public class ScriptMenu extends JMenu {
    private final Path directory;

    public ScriptMenu(String text, Path directory) {
        setText(text);

        this.directory = directory;

        addMenuListener(new MenuAdapter() {
            @Override
            public void menuSelected(MenuEvent event) {
                updateItems();
            }
        });
    }

    private void updateItems() {
        removeAll();

        try {
            Files.list(directory).forEach(child -> {
                if (Files.isDirectory(child)) {
                    add(new ScriptMenu(child.getFileName().toString(), child));
                } else {
                    add(new RunScriptAction(child));
                }
            });
        } catch (IOException e) {
            //TODO: What now?
        }
    }
}
