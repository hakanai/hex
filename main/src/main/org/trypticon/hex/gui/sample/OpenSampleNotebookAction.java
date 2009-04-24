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
