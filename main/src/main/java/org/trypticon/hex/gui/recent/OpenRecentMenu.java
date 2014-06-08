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

package org.trypticon.hex.gui.recent;

import java.nio.file.Path;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;

import org.trypticon.hex.gui.HexApplication;
import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.MenuAdapter;

/**
 * Menu containing the recently-opened documents.
 */
public class OpenRecentMenu extends JMenu {
    public OpenRecentMenu(HexApplication application) {
        setText(Resources.getString("OpenRecent.name"));
        RecentDocumentsModel model = application.getRecentDocumentsModel();
        add(new ClearOpenRecentMenuAction(model));

        int numFixedItems = getMenuComponentCount();

        addMenuListener(new MenuAdapter() {
            @Override
            public void menuSelected(MenuEvent event) {
                while (getMenuComponentCount() > numFixedItems) {
                    remove(0);
                }

                List<Path> recentDocuments = model.getRecentDocuments();
                int index = 0;
                for (Path recent : recentDocuments) {
                    JMenuItem item = add(new OpenRecentDocumentAction(application, recent));
                    add(item, index++);
                }
                if (!recentDocuments.isEmpty()) {
                    add(new JPopupMenu.Separator(), index);
                }
            }
        });
    }
}
