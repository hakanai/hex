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

package org.trypticon.hex.scripting;

import java.awt.Font;

import com.jtechdev.macwidgets.MacFontUtils;
import com.jtechdev.macwidgets.plaf.SourceListTreeUI;

/**
 * Fixing the appearance of the source list.
 *
 * @author trejkaz .
 */
public class FixedSourceListTreeUI extends SourceListTreeUI {
    public FixedSourceListTreeUI() {
        setCategoryFont(MacFontUtils.BOLD_LABEL_FONT.deriveFont(Font.BOLD, 11.0f));
        setItemFont(MacFontUtils.DEFAULT_LABEL_FONT.deriveFont(13.0f));
        setItemSelectedFont(MacFontUtils.DEFAULT_LABEL_FONT.deriveFont(Font.BOLD, 13.0f));
    }
}
