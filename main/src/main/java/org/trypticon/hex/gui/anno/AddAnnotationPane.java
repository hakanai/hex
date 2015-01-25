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

package org.trypticon.hex.gui.anno;

import java.awt.Component;
import java.util.List;

import org.jdesktop.swingx.renderer.StringValue;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.interpreters.InterpreterInfo;
import org.trypticon.hex.interpreters.MasterInterpreterStorage;
import org.trypticon.hex.util.Format;
import org.trypticon.hex.util.Predicate;
import org.trypticon.hex.util.Strings;
import org.trypticon.hex.util.swingsupport.SelectObjectPane;

/**
 * Pane allows selecting an annotation to add.
 */
public class AddAnnotationPane extends SelectObjectPane<InterpreterInfo> {

    @Override
    protected List<InterpreterInfo> createList() {
        return new MasterInterpreterStorage().getInterpreterInfos();
    }

    @Override
    protected StringValue createDisplayConverter() {
        return element -> ((InterpreterInfo) element).toLocalisedString(Format.LONG);
    }

    @Override
    protected Predicate<InterpreterInfo> createFilterPredicate(String filterText) {
        final String[] textFragments = Strings.splitOnWhitespace(filterText);
        return info -> {
            String shortName = info.toLocalisedString(Format.SHORT);
            String longName = info.toLocalisedString(Format.LONG);
            for (String fragment : textFragments) {
                if (Strings.containsIgnoreCase(shortName, fragment)) {
                    continue;
                }
                if (Strings.containsIgnoreCase(longName, fragment)) {
                    continue;
                }
                return false;
            }
            return true;
        };
    }

    /**
     * Shows the annotation pane in a dialog.
     *
     * @param parentComponent the parent component.
     * @return the chosen interpreter.
     */
    public InterpreterInfo showDialog(Component parentComponent) {
        return showDialog(parentComponent,
                          Resources.getString("AddAnnotation.nameWithoutEllipsis"),
                          Resources.getString("AddAnnotation.okButton"));
    }

}
