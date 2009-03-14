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
