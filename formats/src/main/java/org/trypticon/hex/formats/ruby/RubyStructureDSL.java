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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.TestOnly;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
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

    @Nullable
    private final String scriptlet;

    @Nullable
    private final Path scriptFile;

    private RubyStructureDSL(@NonNls @Nonnull String scriptlet) {
        this.scriptlet = scriptlet;
        this.scriptFile = null;
    }

    private RubyStructureDSL(@Nonnull Path scriptFile) {
        this.scriptlet = null;
        this.scriptFile = scriptFile;
    }

    @TestOnly
    public static Structure loadScriptlet(@NonNls String scriptlet) {
        return new RubyStructureDSL(scriptlet).createStructure();
    }

    public static Structure loadFromFile(@NonNls Path file) {
        return new RubyStructureDSL(file).createStructure();
    }

    private Structure createStructure() {
        // Default behaviour of ScriptingContainer seems broken, but we can improve it.
        ScriptingContainer container = new ScriptingContainer(LocalContextScope.THREADSAFE,
                                                              LocalVariableBehavior.PERSISTENT);
        try {
            container.put("$interpreter_storage", new MasterInterpreterStorage());

            // Set up the library scripts will have by default.
            @NonNls
            String fileName = RubyStructureDSL.class.getPackage().getName().replace('.', '/') + "/structure_dsl.rb";
            container.runScriptlet(PathType.CLASSPATH, fileName);

            // Run the script itself, which should return a Structure instance.
            Object instance;
            if (scriptFile != null) {
                try (InputStream resource = Files.newInputStream(scriptFile)) {
                    instance = container.runScriptlet(resource, pathToJRubyPath(scriptFile));
                }
            } else {
                instance = container.runScriptlet(scriptlet);
            }

            return container.getInstance(instance, Structure.class);
        } catch (IOException | RuntimeException e) {
            String source = scriptFile != null ? scriptFile.toString() : scriptlet;
            throw new RuntimeException("Error loading script: \n" + source, e);
        } finally {
            container.terminate();
        }
    }

    /**
     * Try to determine a file path which JRuby will find appropriate for use.
     * It has a slightly different notion of how files work, when compared with Java.
     *
     * @param path the file path.
     * @return the path to give JRuby.
     */
    private static String pathToJRubyPath(Path path) {
        String scheme = path.toUri().getScheme();
        switch (scheme) {
            case "file":
                return path.toString();
            case "jar": {
                // Workaround for quirky JRuby behaviour with jar URIs
                URL jarUrl;
                try {
                    jarUrl = path.toUri().toURL();
                } catch (MalformedURLException e) {
                    throw new IllegalStateException("Malformed URL from URI: " + path.toUri(), e);
                }

                try {
                    JarURLConnection connection = (JarURLConnection) jarUrl.openConnection();
                    return "uri:classloader:" + connection.getJarEntry().getName();
                } catch (IOException e) {
                    throw new UncheckedIOException("Couldn't open jar: " + jarUrl, e);
                }
            }
            default:
                return "uri:" + path.toUri();
        }
    }
}
