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

package org.trypticon.hex.gui.scripting;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;

import org.trypticon.hex.gui.util.MenuAdapter;
import org.trypticon.hex.util.swingsupport.GuiLocale;

/**
 * A script menu.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class ScriptMenu extends JMenu {
    private final List<Path> directories;

    @Nullable
    private List<Component> staticItems;

    public ScriptMenu(String text, List<Path> directories) {
        setText(text);

        this.directories = new ArrayList<>(directories);

        addMenuListener(new MenuAdapter() {
            @Override
            public void menuSelected(MenuEvent event) {
                updateItems();
            }
        });
    }

    /**
     * Records the items currently in the menu as items which will always be re-added
     * to the end of the menu when it is updated.
     */
    public void useCurrentItemsAsStaticItems() {
        staticItems = Arrays.asList(getMenuComponents());
    }

    private void updateItems() {
        removeAll();

        Map<String, List<Path>> listings = directories.parallelStream()
            .flatMap(ScriptMenu::safeDirectoryList)
            .filter(p -> Files.isDirectory(p) || p.getFileName().toString().endsWith(".rb")) //NON-NLS
            .collect(Collectors.groupingBy(p -> p.getFileName().toString(),
                                           () -> new TreeMap<>(Collator.getInstance(GuiLocale.get())),
                                           Collectors.toList()));

        listings.entrySet().stream().forEach(entry -> {
            String fileName = entry.getKey();
            List<Path> files = entry.getValue();

            Path firstFile = files.get(0);
            if (Files.isDirectory(firstFile)) {
                add(new ScriptMenu(fileName, files.stream()
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList())));
            } else {
                add(new RunScriptAction(fileName, firstFile));
            }
        });

        if (staticItems != null) {
            staticItems.forEach(this::add);
        }
    }

    @WillNotClose
    // Closed by the caller.
    @SuppressWarnings("StreamResourceLeak")
    private static Stream<Path> safeDirectoryList(Path directory) {
        try {
            return Files.list(directory);
        } catch (NotDirectoryException e) {
            return Stream.empty();
        } catch (IOException e) {
            Logger.getLogger(ScriptMenu.class.getName()).log(Level.WARNING, "Couldn't read directory: " + directory, e);
            return Stream.empty();
        }
    }
}
