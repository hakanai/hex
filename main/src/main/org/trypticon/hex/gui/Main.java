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

package org.trypticon.hex.gui;

import java.io.File;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookStorage;

/**
 * Main entry point.
 *
 * @author trejkaz
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new Main().execute(args);
    }

    public void execute(Object[] args) throws Exception {
        PLAFBootstrap.init();

        Notebook notebook;
        if (args.length == 1 && args[0] instanceof String) {
            // TODO: Support a URL here too.
            // TODO: Support binary here too. Find a way to distinguish this in this context.
            File file = new File((String) args[0]);
            notebook = new NotebookStorage().read(file.toURI().toURL());
        } else {
            notebook = new Notebook(getClass().getClassLoader().getResource("org/trypticon/hex/gui/Sample.class"));
        }

        HexFrame.openNotebook(notebook);
    }
}
