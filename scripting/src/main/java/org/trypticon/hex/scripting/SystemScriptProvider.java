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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import com.jtechdev.macwidgets.SourceListCategory;
import com.jtechdev.macwidgets.SourceListItem;
import com.jtechdev.macwidgets.SourceListModel;
import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.util.LoggerUtils;

/**
 * Model of scripts stored inside the application.
 *
 * @author trejkaz
 */
public class SystemScriptProvider {

    public void populate(SourceListModel model) {
        //TODO: i18n
        SourceListCategory category = new SourceListCategory("System Scripts");
        model.addCategory(category);

        populateItems("/org/trypticon/hex/formats/repository/", model, category);
    }

    private void populateItems(@NonNls String root, SourceListModel model, Object parent) {
        try (InputStream stream =
                SystemScriptProvider.class.getResourceAsStream(root + "entries.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.US_ASCII))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith("/")) {
                    SourceListItem item = new SourceListItem(line.substring(0, line.length() - 1));
                    addItemToParent(model, item, parent);
                    populateItems(root + line, model, item);
                } else {
                    addItemToParent(model, new SourceListItem(line), parent);
                }
            }

        } catch (IOException e) {
            LoggerUtils.get().log(Level.SEVERE, "Couldn't read resources from classpath", e);
        }
    }

    private void addItemToParent(SourceListModel model, SourceListItem item, Object parent) {
        if (parent instanceof SourceListCategory) {
            model.addItemToCategory(item, (SourceListCategory) parent);
        } else {
            model.addItemToItem(item, (SourceListItem) parent);
        }
    }
}
