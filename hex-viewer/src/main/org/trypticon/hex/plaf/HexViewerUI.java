/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.trypticon.hex.plaf;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.plaf.ComponentUI;

import org.trypticon.hex.HexViewer;

/**
 * Base abstract class for pluggable look and feels for the hex viewer.
 *
 * @author trejkaz
 */
public abstract class HexViewerUI extends ComponentUI {

    /**
     * Converts the given location in the model to a place in the view coordinate system.
     *
     * @param viewer the viewer.
     * @param pos the position.
     * @return the bounds of the given position.
     */
    public abstract Rectangle modelToView(HexViewer viewer, long pos);

    /**
     * Converts the given place in the view coordinate system to the nearest representative location in the model.
     *
     * @param viewer the viewer.
     * @param point the point.
     * @return the offset from the start of the binary, >= 0.
     */
    public abstract long viewToModel(HexViewer viewer, Point point);
}
