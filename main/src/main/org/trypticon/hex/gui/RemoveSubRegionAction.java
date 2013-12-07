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

package org.trypticon.hex.gui;

import java.awt.event.ActionEvent;

import org.trypticon.hex.HexViewer;
import org.trypticon.hex.anno.GroupAnnotation;
import org.trypticon.hex.util.swingsupport.ActionException;
import org.trypticon.hex.util.swingsupport.BaseAction;

/**
 * Action to remove a sub-region.
 *
 * @author trejkaz
 */
public class RemoveSubRegionAction extends BaseAction {
    public RemoveSubRegionAction() {
        putValue(NAME, "Remove Sub-Region");
    }

    @Override
    protected void doAction(ActionEvent event) throws Exception {
        HexFrame frame = HexFrame.findActiveFrame();
        if (frame == null || frame.getNotebookPane() == null) {
            throw new ActionException("To remove a sub-region, focus must be on the hex viewer.");
        }

        HexViewer viewer = frame.getNotebookPane().getViewer();

        long position = viewer.getSelectionModel().getCursor();

        GroupAnnotation group = viewer.getAnnotations().getRootGroup().findDeepestGroupAnnotationAt(position);
        if (group == null) {
            throw new ActionException("To remove a group annotation, the cursor must be at a group annotation.");
        }

        viewer.getAnnotations().remove(group);
    }
}
