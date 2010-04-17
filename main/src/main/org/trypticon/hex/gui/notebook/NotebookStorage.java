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

package org.trypticon.hex.gui.notebook;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;

import org.trypticon.hex.interpreters.InterpreterStorage;
import org.trypticon.hex.interpreters.MasterInterpreterStorage;
import org.trypticon.hex.util.URLUtils;
import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Support for reading and writing {@link DefaultNotebook}s to files.
 *
 * @author trejkaz
 */
public class NotebookStorage {
    private final Yaml yaml;

    public NotebookStorage() {
        InterpreterStorage interpreterStorage = new MasterInterpreterStorage();

        BaseConstructor constructor = new ExtendedConstructor(interpreterStorage);

        Representer representer = new ExtendedRepresenter(interpreterStorage);
        DumperOptions dumperOptions = new DumperOptions();

        yaml = new Yaml(new Loader(constructor),
                        new Dumper(representer, dumperOptions));
    }

    public Notebook read(Reader reader) throws IOException {
        try {
            return (Notebook) yaml.load(reader);
        } catch (YAMLException e) {
            rethrow(e);
            return null; // actually unreachable, compiler isn't smart enough.
        }
    }

    public Notebook read(URL url) throws IOException {
        Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
        try {
            Notebook notebook = read(reader);
            notebook.setNotebookLocation(url);
            return notebook;
        } finally {
            reader.close();
        }
    }

    public void write(Notebook notebook, Writer writer) throws IOException {
        try {
            yaml.dump(notebook, writer);
        } catch (YAMLException e) {
            rethrow(e);
        }
    }

    private void write(Notebook notebook, File file) throws IOException {
        Writer writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), "UTF-8");
        try {
            write(notebook, writer);
        } finally {
            writer.close();
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

            Writer writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            try {
                write(notebook, writer);
            } finally {
                writer.close();
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
