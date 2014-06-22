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

package org.trypticon.hex.gui.find;

import java.awt.Component;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jdesktop.swingx.renderer.StringValue;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.SelectObjectPane;
import org.trypticon.hex.gui.util.Strings;

/**
 * Pane to select a character encoding.
 *
 * @author trejkaz
 */
class SelectEncodingPane extends SelectObjectPane<Charset> {
    @Override
    protected List<Charset> createList() {
        return new ArrayList<>(Charset.availableCharsets().values());
    }

    @Override
    protected StringValue createDisplayConverter() {
        return element -> ((Charset) element).name();
    }

    @Override
    protected Predicate<Charset> createFilterPredicate(String filterText) {
        final String[] textFragments = Strings.splitOnWhitespace(filterText);
        return charset -> {
            for (String fragment : textFragments) {
                if (Strings.containsIgnoreCase(charset.name(), fragment)) {
                    continue;
                }

                boolean found = false;
                for (String alias : charset.aliases()) {
                    if (Strings.containsIgnoreCase(alias, fragment)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }

                return false;
            }
            return true;
        };
    }

    /**
     * Shows the encoding selection pane in a dialog.
     *
     * @param parentComponent the parent component.
     * @return the chosen encoding.
     */
    public Charset showDialog(Component parentComponent) {
        return showDialog(parentComponent,
                          Resources.getString("SelectEncoding.title"),
                          Resources.getString("SelectEncoding.okButton"));
    }
}
