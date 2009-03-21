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

package org.trypticon.hex.gui.notebook;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jvyaml.DefaultYAMLConfig;
import org.jvyaml.DefaultYAMLFactory;
import org.jvyaml.Representer;
import org.jvyaml.Serializer;
import org.jvyaml.YAML;
import org.jvyaml.YAMLConfig;
import org.jvyaml.YAMLFactory;
import org.jvyaml.Constructor;
import org.jvyaml.Composer;
import org.jvyaml.YAMLException;

import org.trypticon.hex.anno.InterpretorStorage;
import org.trypticon.hex.anno.MasterInterpretorStorage;
import org.trypticon.hex.util.URLUtils;

/**
 * Support for reading and writing {@link Notebook}s to files.
 *
 * @author trejkaz
 */
public class NotebookStorage {
    private final InterpretorStorage interpretorStorage = new MasterInterpretorStorage();
    private final YAMLConfig config;
    private final YAMLFactory factory;

    public NotebookStorage() {
        config = new DefaultYAMLConfig();

        factory = new DefaultYAMLFactory() {
            @Override
            public Constructor createConstructor(Composer composer) {
                return new ExtendedConstructorImpl(composer, interpretorStorage);
            }

            @Override
            public Representer createRepresenter(Serializer serializer, YAMLConfig yamlConfig) {
                return new ExtendedRepresenterImpl(serializer, yamlConfig, interpretorStorage);
            }
        };
    }

    public Notebook read(Reader reader) throws IOException {
        try {
            return (Notebook) YAML.load(reader, factory, config);
        } catch (YAMLException e) {
            rethrow(e);
            return null; // actually unreachable, compiler isn't smart enough.
        }
    }

    public Notebook read(URL url) throws IOException {
        InputStream stream = url.openStream();
        try {
            Notebook notebook = read(new InputStreamReader(stream, "UTF-8"));
            notebook.setNotebookLocation(url);
            return notebook;
        } finally {
            stream.close();
        }
    }

    public void write(Notebook notebook, Writer writer) throws IOException {
        try {
            YAML.dump(notebook, writer, factory, config);
        } catch (YAMLException e) {
            rethrow(e);
        }
    }

    private void write(Notebook notebook, File file) throws IOException {
        OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        try {
            write(notebook, new OutputStreamWriter(stream, "UTF-8"));
        } finally {
            stream.close();
        }
    }

    public void write(Notebook notebook, URL url) throws IOException {
        // Workaround for Java Bug 4814217 - file protocol writing does not work.
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4814217
        if ("file".equals(url.getProtocol())) {
            write(notebook, URLUtils.toFile(url));
        } else {
            URLConnection connection = url.openConnection();
            connection.setDoInput(false);
            connection.setDoOutput(true);
            connection.connect();

            OutputStream stream = connection.getOutputStream();
            try {
                write(notebook, new OutputStreamWriter(stream, "UTF-8"));
            } finally {
                stream.close();
            }
        }
        notebook.setNotebookLocation(url);
    }

    /**
     * The YAML library wraps a runtime exception around the real problem.
     * We unwrap it because propagating the real exception is usually better.
     *
     * @param e the exception.
     * @throws IOException if the cause is actually an IOException.
     * @throws YAMLException otherwise.
     */
    private static void rethrow(YAMLException e) throws IOException {
        if (e.getCause() instanceof IOException) {
            throw (IOException) e.getCause();
        } else {
            throw e;
        }
    }
}
