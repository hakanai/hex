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

package org.trypticon.hex.gui.sample;

import java.awt.event.ActionEvent;

import org.trypticon.hex.anno.AnnotationCollection;
import org.trypticon.hex.anno.SimpleMutableAnnotation;
import org.trypticon.hex.anno.primitive.UIntInterpretorBE;
import org.trypticon.hex.gui.HexFrame;
import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.util.swingsupport.BaseAction;

/**
 * Action to open a sample document.
 *
 * @author trejkaz
 */
public class OpenSampleNotebookAction extends BaseAction {
    public OpenSampleNotebookAction() {
        putValue(NAME, "Open Sample Notebook");
        putValue(MNEMONIC_KEY, (int) 's');
    }

    protected void doAction(ActionEvent event) throws Exception {
        String resourcePath = Sample.class.getName().replace('.', '/') + ".class";
        Notebook notebook = new Notebook(getClass().getClassLoader().getResource(resourcePath));

        AnnotationCollection annotations = notebook.getAnnotations();
        annotations.add(new SimpleMutableAnnotation(0, new UIntInterpretorBE(), "magic number"));

        HexFrame.openNotebook(notebook);
    }
}
