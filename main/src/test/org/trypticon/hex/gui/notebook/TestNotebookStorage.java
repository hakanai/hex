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

import java.io.*;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.SimpleMutableAnnotationGroup;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.nulls.NullInterpretor;
import org.trypticon.hex.anno.primitive.PrimitiveInterpretors;
import org.trypticon.hex.anno.strings.StringInterpretor;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link NotebookStorage}.
 *
 * @author trejkaz
 */
public class TestNotebookStorage {
    private NotebookStorage storage = new NotebookStorage();

    @Test
    public void testRoundTrip() throws Exception {
        Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"));
        AnnotationCollection annotations = notebook.getAnnotations();
        annotations.add(new SimpleMutableAnnotation(5, 4, new NullInterpretor(), "Test"));
        annotations.add(new SimpleMutableAnnotation(9, 4, PrimitiveInterpretors.UINT32_LE, null));
        annotations.add(new SimpleMutableAnnotation(13, 4, new StringInterpretor("utf8"), null));

        // TODO: Test nested groups (not supported yet.)
        annotations.add(new SimpleMutableAnnotationGroup(9, 8, "Test Group"));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        storage.write(notebook, stream);

        byte[] content = stream.toByteArray();

        System.out.println(new String(content));

        InputStream inStream = new ByteArrayInputStream(content);
        Notebook churned = storage.read(inStream);

        assertEquals("Wrong binary location", notebook.getBinaryLocation(), churned.getBinaryLocation());
        assertEquals("Wrong annotations", notebook.getAnnotations().getAll(), churned.getAnnotations().getAll());
        assertEquals("Wrong groups", notebook.getAnnotations().getGroups(), churned.getAnnotations().getGroups());
    }

    @Test
    public void testFileStorage() throws Exception {
        File tmpFile = File.createTempFile("temp", ".xml");
        try {
            URL tmpFileURL = tmpFile.toURI().toURL();
            Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"));

            storage.write(notebook, tmpFileURL);

            Notebook churned = storage.read(tmpFileURL);
            assertEquals("Wrong binary location", notebook.getBinaryLocation(), churned.getBinaryLocation());
        } finally {
            if (!tmpFile.delete()) {
                System.err.println("Error deleting temp file: " + tmpFile);
            }
        }
    }

    @Test(expected=IOException.class)
    public void testIOExceptionOnReading() throws Exception {
        storage.read(new BrokenInputStream());
    }

    @Test(expected=IOException.class)
    @Ignore("requires a fix to JvYAMLb: http://code.google.com/p/jvyamlb/issues/detail?id=7")
    public void testIOExceptionOnWriting() throws Exception {
        Notebook notebook = new DefaultNotebook(new URL("http://example.com/biscuits.dat.xml"));
        storage.write(notebook, new BrokenOutputStream());
    }

    private static class BrokenInputStream extends InputStream {
        public int read() throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public int read(byte[] bytes, int i, int i1) throws IOException {
            throw new IOException("Broken");
        }

        public void close() throws IOException {
            throw new IOException("Broken");
        }
    }

    private static class BrokenOutputStream extends OutputStream {
        public void write(int i) throws IOException {
            throw new IOException("Broken");
        }

        @Override
        public void write(byte[] bytes, int i, int i1) throws IOException {
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
