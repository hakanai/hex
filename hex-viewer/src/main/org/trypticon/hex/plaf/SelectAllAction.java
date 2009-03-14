/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.trypticon.hex.HexViewer;

/**
 * Action to select the entire content of the viewer.
 *
 * @author trejkaz
 */
class SelectAllAction extends AbstractAction {
    public void actionPerformed(ActionEvent event) {
        HexViewer viewer = (HexViewer) event.getSource();
        viewer.getSelectionModel().setCursor(viewer.getBinary().length() - 1);
        viewer.getSelectionModel().setCursorAndExtendSelection(0);
    }
}
