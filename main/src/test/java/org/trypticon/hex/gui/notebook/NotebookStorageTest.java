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
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.trypticon.hex.anno.Annotation;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.CommonAttributes;
import org.trypticon.hex.anno.SimpleAnnotation;
import org.trypticon.hex.anno.SimpleGroupAnnotation;
import org.trypticon.hex.gui.anno.CustomAttributes;
import org.trypticon.hex.gui.anno.ExtendedAnnotationCollection;
import org.trypticon.hex.gui.anno.ParametricStyle;
import org.trypticon.hex.interpreters.nulls.NullInterpreter;
import org.trypticon.hex.interpreters.primitives.PrimitiveInterpreters;
import org.trypticon.hex.interpreters.strings.StringInterpreter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.trypticon.hex.formats.ruby.AnnotationTestUtils.sameAnnotation;

/**
 * Tests for {@link NotebookStorage}.
 *
 * @author trejkaz
 */
public class NotebookStorageTest {
    private Logger logger;
    private NotebookStorage storage;

    @BeforeEach
    public void setUp() {
        logger = Logger.getLogger(NotebookStorageTest.class.getName());
        storage = new NotebookStorage();
    }

    @Test
    public void testRoundTrip() throws Exception {
        Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"),
                                                new ExtendedAnnotationCollection(100));
        AnnotationCollection annotations = notebook.getAnnotations();
        Annotation leaf1 = new SimpleAnnotation(5, 4, new NullInterpreter());
        leaf1.set(CommonAttributes.NOTE, "Test");
        leaf1.set(CustomAttributes.CUSTOM_STYLE, new ParametricStyle(ParametricStyle.StrokeStyle.DASHED,
                                                                     Color.red, Color.white));
        annotations.add(leaf1);
        annotations.add(new SimpleAnnotation(9, 4, PrimitiveInterpreters.UINT32_LE));
        annotations.add(new SimpleAnnotation(13, 4, new StringInterpreter(StandardCharsets.UTF_8)));
        Annotation group1 = new SimpleGroupAnnotation(9, 8);
        group1.set(CommonAttributes.NOTE, "Test Group");
        group1.set(CustomAttributes.CUSTOM_STYLE, new ParametricStyle(ParametricStyle.StrokeStyle.SOLID,
                                                                      Color.lightGray, Color.white));
        annotations.add(group1);

        StringWriter writer = new StringWriter();
        storage.write(notebook, writer);

        String content = writer.toString();

        logger.fine(content);

        Reader reader = new StringReader(content);
        Notebook churned = storage.read(reader);

        assertEquals(notebook.getBinaryLocation(), churned.getBinaryLocation());
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
            assertThat(churned.getBinaryLocation(), is(notebook.getBinaryLocation()));
        } finally {
            if (!tmpFile.delete()) {
                logger.warning("Error deleting temp file: " + tmpFile);
            }
        }
    }

    @Test
    public void testIOExceptionOnReading() {
        assertThrows(IOException.class, () -> storage.read(new BrokenReader()));
    }

    @Test
    public void testIOExceptionOnWriting() throws Exception {
        Notebook notebook = new DefaultNotebook(new URL("https://example.com/biscuits.dat.xml"),
                                                new ExtendedAnnotationCollection(100));
        assertThrows(IOException.class, () -> storage.write(notebook, new BrokenWriter()));
    }

    private static class BrokenReader extends Reader {
        @Override
        public int read() throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(@Nonnull CharBuffer target) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(@Nonnull char[] cbuf) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(@Nonnull char[] cbuf, int off, int len) throws IOException {
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
        public void write(@Nonnull char[] cbuf) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(@Nonnull char[] cbuf, int off, int len) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(@Nonnull String str) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(@Nonnull String str, int off, int len) throws IOException {
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
