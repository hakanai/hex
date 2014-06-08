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

package org.trypticon.hex.gui.notebook;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.ArrayList;

import org.junit.Test;

import org.trypticon.hex.anno.MutableAnnotationCollection;
import org.trypticon.hex.gui.anno.DefaultExtendedAnnotation;
import org.trypticon.hex.gui.anno.DefaultExtendedGroupAnnotation;
import org.trypticon.hex.gui.anno.ExtendedAnnotationCollection;
import org.trypticon.hex.gui.anno.ParametricStyle;
import org.trypticon.hex.interpreters.nulls.NullInterpreter;
import org.trypticon.hex.interpreters.primitives.PrimitiveInterpreters;
import org.trypticon.hex.interpreters.strings.StringInterpreter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.trypticon.hex.formats.ruby.AnnotationTestUtils.sameAnnotation;

/**
 * Tests for {@link NotebookStorage}.
 *
 * @author trejkaz
 */
public class NotebookStorageTest {

    private NotebookStorage storage = new NotebookStorage();

    @Test
    public void testRoundTrip() throws Exception {
        Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"),
                                                new ExtendedAnnotationCollection(100));
        MutableAnnotationCollection annotations = notebook.getAnnotations();
        annotations.add(new DefaultExtendedAnnotation(5, 4, new NullInterpreter(), "Test",
                                                      new ParametricStyle(ParametricStyle.StrokeStyle.DASHED,
                                                                          Color.red, Color.white)));
        annotations.add(new DefaultExtendedAnnotation(9, 4, PrimitiveInterpreters.UINT32_LE, null));
        annotations.add(new DefaultExtendedAnnotation(13, 4, new StringInterpreter("utf8"), null));
        annotations.add(new DefaultExtendedGroupAnnotation(9, 8, "Test Group", new ArrayList<>(4),
                                                           new ParametricStyle(ParametricStyle.StrokeStyle.SOLID,
                                                                               Color.lightGray, Color.white)));

        StringWriter writer = new StringWriter();
        storage.write(notebook, writer);

        String content = writer.toString();

        System.out.println(content);

        Reader reader = new StringReader(content);
        Notebook churned = storage.read(reader);

        assertEquals("Wrong binary location", notebook.getBinaryLocation(), churned.getBinaryLocation());
        assertThat(churned.getAnnotations().getRootGroup(),
                   is(sameAnnotation(notebook.getAnnotations().getRootGroup())));
    }

    @Test
    public void testFileStorage() throws Exception {
        File tmpFile = File.createTempFile("temp", ".xml");
        try {
            URL tmpFileURL = tmpFile.toURI().toURL();
            Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"), new ExtendedAnnotationCollection(100));

            storage.write(notebook, tmpFileURL);

            Notebook churned = storage.read(tmpFileURL);
            assertEquals("Wrong binary location", notebook.getBinaryLocation(), churned.getBinaryLocation());
        } finally {
            if (!tmpFile.delete()) {
                System.err.println("Error deleting temp file: " + tmpFile);
            }
        }
    }

    @Test
    public void testSpaceInName_NewNotebook() throws Exception {
        Notebook notebook = new DefaultNotebook(new File("some file.dat").toURI().toURL());
        assertEquals("Wrong notebook name", "New: some file.dat", notebook.getName());
    }

    @Test
    public void testSpaceInName_SavedNotebook() throws Exception {
        Notebook notebook = new DefaultNotebook(new File("some file.dat").toURI().toURL());
        notebook.setNotebookLocation(new File("My Notebook.hex").toURI().toURL());
        assertEquals("Wrong notebook name", "My Notebook.hex", notebook.getName());
    }

    @Test(expected = IOException.class)
    public void testIOExceptionOnReading() throws Exception {
        storage.read(new BrokenReader());
    }

    @Test(expected = IOException.class)
    public void testIOExceptionOnWriting() throws Exception {
        Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"),
                                                new ExtendedAnnotationCollection(100));
        storage.write(notebook, new BrokenWriter());
    }

    private static class BrokenReader extends Reader {
        @Override
        public int read() throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(CharBuffer target) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(char[] cbuf) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Broken");
        }
    }

    private static class BrokenWriter extends Writer {
        @Override
        public void write(int i) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(char[] cbuf) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(String str) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public Writer append(CharSequence csq) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public Writer append(char c) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void flush() throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Broken");
        }
    }
}
