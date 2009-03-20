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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jvyaml.DefaultYAMLConfig;
import org.jvyaml.DefaultYAMLFactory;
import org.jvyaml.Representer;
import org.jvyaml.RepresenterImpl;
import org.jvyaml.Serializer;
import org.jvyaml.YAML;
import org.jvyaml.YAMLConfig;
import org.jvyaml.YAMLFactory;
import org.jvyaml.YAMLNodeCreator;
import org.jvyaml.Constructor;
import org.jvyaml.Composer;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.Interpretor;
import org.trypticon.hex.anno.InterpretorStorage;
import org.trypticon.hex.anno.MasterInterpretorStorage;

/**
 * Support for reading and writing {@link Notebook}s to files.
 *
 * @author trejkaz
 */
public class NotebookStorage {
    private final InterpretorStorage interpretorStorage = new MasterInterpretorStorage();
    private YAMLConfig config;
    private YAMLFactory factory;

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
        return (Notebook) YAML.load(reader, factory, config);
    }

    public Notebook read(URL url) throws IOException {
        InputStream stream = url.openStream();
        try {
            return read(new InputStreamReader(stream, "UTF-8"));
        } finally {
            stream.close();
        }
    }

    public void write(Notebook notebook, Writer writer) throws IOException {
        YAML.dump(notebook, writer, factory, config);
    }

    public void write(Notebook notebook, URL url) throws IOException {
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
}
