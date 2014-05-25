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

package org.trypticon.hex.formats.ruby;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NonNls;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.interpreters.MasterInterpreterStorage;

/**
 * Java entry point to a Ruby DSL for creating structures.
 *
 * @author trejkaz
 */
public class RubyStructureDSL {
    private final String scriptlet;

    private RubyStructureDSL(@NonNls String scriptlet) {
        this.scriptlet = scriptlet;
    }

    private RubyStructureDSL(URL scriptLocation) {
        // TODO: Doing it this way actually loses the link to the script when something goes wrong.
        this(loadURL(scriptLocation));
    }

    public static Structure load(@NonNls String scriptlet) {
        return new RubyStructureDSL(scriptlet).createStructure();
    }

    public static Structure load(URL scriptLocation) {
        return new RubyStructureDSL(scriptLocation).createStructure();
    }

    private static String loadURL(URL location) {
        try {
            InputStream stream = location.openStream();
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            byte[] buffer = new byte[64 * 1024];
            int bytesRead;
            while ((bytesRead = stream.read(buffer)) != -1) {
                temp.write(buffer, 0, bytesRead);
            }
            return temp.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException("URL was not accessible: " + location, e);
        }
    }

    private Structure createStructure() {
        ScriptingContainer container = new ScriptingContainer();
        container.setOutput(System.out);

        // Set up the library scripts will have by default.
        String basePath = RubyStructureDSL.class.getPackage().getName().replace('.', '/');
        @NonNls
        String fileName = basePath + "/structure_dsl.rb";
        container.put("$interpreter_storage", new MasterInterpreterStorage());
        container.runScriptlet(PathType.CLASSPATH, fileName);

        // Run the script itself, which should return a Structure instance.
        try {
            Object instance = container.runScriptlet(scriptlet);
            return container.getInstance(instance, Structure.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error loading script: \n" + scriptlet, e);
        }
    }
}
