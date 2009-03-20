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

import java.net.URL;
import java.io.StringWriter;
import java.io.StringReader;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.NullInterpretor;
import org.trypticon.hex.anno.primitive.PrimitiveInterpretors;

/**
 * Tests for {@link NotebookStorage}.
 *
 * @author trejkaz
 */
public class TestNotebookStorage {

    @Test
    public void testRoundTrip() throws Exception {
        Notebook notebook = new Notebook(new URL("http://example.com/biscuits.dat.xml"));
        AnnotationCollection annotations = notebook.getAnnotations();
        annotations.add(new SimpleMutableAnnotation(5, new NullInterpretor(4), "Test"));
        annotations.add(new SimpleMutableAnnotation(9, PrimitiveInterpretors.UINT32_LE, null));

        NotebookStorage storage = new NotebookStorage();

        StringWriter writer = new StringWriter();
        storage.write(notebook, writer);

        String content = writer.toString();

        //System.out.println(content);

        StringReader reader = new StringReader(content);
        Notebook churned = storage.read(reader);

        assertEquals("Wrong binary location", notebook.getBinaryLocation(), churned.getBinaryLocation());
        assertEquals("Wrong annotations", notebook.getAnnotations().getAll(), churned.getAnnotations().getAll());
    }
}
