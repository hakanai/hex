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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * Model to track the recent documents.
 */
public class RecentDocumentsModel {
    private static final int LIMIT = 10;

    private final Preferences preferences = Preferences.userNodeForPackage(RecentDocumentsModel.class);

    private final EventListenerList listenerList = new EventListenerList();

    private final List<Path> recentList = new ArrayList<>(10);

    public RecentDocumentsModel() {
        int recentListSize = preferences.getInt("count", 0);
        for (int i = 0; i < recentListSize; i++) {
            String pathString = preferences.get(String.valueOf(i), null);
            if (pathString != null) {
                Path path = Paths.get(pathString);
                if (Files.exists(path)) {
                    recentList.add(path);
                }
            }
        }
    }

    public List<Path> getRecentDocuments() {
        return Collections.unmodifiableList(recentList);
    }

    public void addRecentDocument(Path location) {
        recentList.add(0, location);
        while (recentList.size() > LIMIT) {
            recentList.remove(LIMIT);
        }
        saveState();
    }

    private void saveState() {
        preferences.putInt("count", recentList.size());
        for (int i = 0; i < recentList.size(); i++) {
            preferences.put(String.valueOf(i), recentList.get(i).toString());
        }
    }

    public void clear() {
        recentList.clear();
        saveState();
    }

    public boolean isEmpty() {
        return recentList.isEmpty();
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(EventListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(EventListener.class, listener);
    }
}
