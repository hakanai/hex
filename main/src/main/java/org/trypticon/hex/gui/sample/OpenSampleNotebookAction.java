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

package org.trypticon.hex.gui.sample;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.EmptyBinary;
import org.trypticon.hex.formats.Repository;
import org.trypticon.hex.formats.Structure;
import org.trypticon.hex.formats.ruby.RubyStructureDSL;
import org.trypticon.hex.gui.HexApplication;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.notebook.DefaultNotebook;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.util.BaseAction;

/**
 * Action to open a sample document.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class OpenSampleNotebookAction extends BaseAction {
    private final HexApplication application;

    public OpenSampleNotebookAction(HexApplication application) {
        this.application = application;
        Resources.localiseAction(this, "OpenSampleNotebook");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        @NonNls
        String resourcePath = Sample.class.getName().replace('.', '/') + ".class";
        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath));
        Notebook notebook = new DefaultNotebook(Paths.get(resource.toURI()));

        application.openNotebook(notebook);

        Binary binary = notebook.getBinary();
        if (binary == null) {
            binary = new EmptyBinary();
        }

        AnnotationCollection annotations = notebook.getAnnotations();

        Structure structure = RubyStructureDSL.loadFromFile(Repository.getRoot().resolve("classfile/class_file.rb"));
        if (structure == null) {
            throw new IllegalStateException("class_file.rb couldn't be loaded");
        }

        annotations.add(structure.drop(binary, 0));
    }
}
