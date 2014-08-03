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

package org.trypticon.hex.gui;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;

import org.trypticon.gum.MacFactory;
import org.trypticon.hex.formats.Repository;
import org.trypticon.hex.gui.datatransfer.CopyAction;
import org.trypticon.hex.gui.datatransfer.CutAction;
import org.trypticon.hex.gui.datatransfer.PasteAction;
import org.trypticon.hex.gui.file.CloseNotebookAction;
import org.trypticon.hex.gui.file.NewNotebookAction;
import org.trypticon.hex.gui.file.OpenNotebookAction;
import org.trypticon.hex.gui.file.RevertToSavedAction;
import org.trypticon.hex.gui.file.SaveNotebookAction;
import org.trypticon.hex.gui.find.FindAction;
import org.trypticon.hex.gui.find.FindNextAction;
import org.trypticon.hex.gui.find.FindPreviousAction;
import org.trypticon.hex.gui.find.JumpToOffsetAction;
import org.trypticon.hex.gui.find.JumpToSelectionAction;
import org.trypticon.hex.gui.recent.OpenRecentMenu;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.gui.scripting.OpenScriptDirectoryAction;
import org.trypticon.hex.gui.scripting.ScriptMenu;
import org.trypticon.hex.gui.undo.GlobalUndoHelper;
import org.trypticon.hex.gui.util.DelegatingAction;
import org.trypticon.hex.gui.util.MenuAdapter;
import org.trypticon.hex.gui.util.platform.Platform;

/**
 * Builds the application menu bar.
 *
 * @author trejkaz
 */
public class MenuBarBuilder {
    private final HexApplication application;

    public MenuBarBuilder(HexApplication application) {
        this.application = application;
    }

    /**
     * Builds the application menu bar.
     *
     * @param frame the frame, or {@code null} if building the frameless menu.
     * @return the menu bar.
     */
    public JMenuBar buildMenuBar(JFrame frame) {
        JMenu fileMenu = new JMenu(Resources.getString(MacFactory.isMac() ? "File.name.mac" : "File.name.other"));
        fileMenu.add(new NewNotebookAction(application));
        fileMenu.add(new OpenNotebookAction(application));
        fileMenu.add(new OpenRecentMenu(application));

        fileMenu.addSeparator();
        fileMenu.add(new CloseNotebookAction());
        Action saveAction = new SaveNotebookAction(application, false);
        if (frame != null) {
            frame.getRootPane().getActionMap().put("save", saveAction);
        }
        fileMenu.add(saveAction);
        fileMenu.add(new SaveNotebookAction(application, true));
        fileMenu.add(new RevertToSavedAction());

        if (!MacFactory.isMac()) {
            fileMenu.addSeparator();
            fileMenu.add(new ExitAction(application));
        }

        JMenu editMenu = new JMenu(Resources.getString("Edit.name"));

        GlobalUndoHelper undoHelper = application.getGlobalUndoHelper();
        editMenu.add(undoHelper.getUndoAction());
        editMenu.add(undoHelper.getRedoAction());

        editMenu.addSeparator();
        editMenu.add(new CutAction());
        editMenu.add(new CopyAction());
        editMenu.add(new PasteAction());
        editMenu.add(new DeleteAction());
        editMenu.add(new DelegatingAction("SelectAll", "select-all"));

        editMenu.addSeparator();
        editMenu.add(new JumpToOffsetAction());

        editMenu.addSeparator();
        JMenu findMenu = new JMenu(Resources.getString("FindMenu.name"));
        findMenu.add(new FindAction());
        findMenu.add(new FindNextAction());
        findMenu.add(new FindPreviousAction());
        findMenu.add(new JumpToSelectionAction());
        editMenu.add(findMenu);

        editMenu.addSeparator();
        editMenu.add(new AddAnnotationAction());
        editMenu.add(new AddSubRegionAction());

        JMenu windowMenu = null;
        if (MacFactory.isMac()) {
            windowMenu = new JMenu(Resources.getString("Window.name"));
            windowMenu.add(new MinimiseAction());
            windowMenu.add(new MaximiseAction());

            windowMenu.addSeparator();
            windowMenu.add(new BringAllToFrontAction());

            // Populate the rest of the Window menu just before it appears.
            final int numFixedItems = windowMenu.getMenuComponentCount();
            final JMenu finalWindowMenu = windowMenu;
            windowMenu.addMenuListener(new MenuAdapter() {
                @Override
                public void menuSelected(MenuEvent event) {
                    while (finalWindowMenu.getMenuComponentCount() > numFixedItems) {
                        finalWindowMenu.remove(numFixedItems);
                    }

                    List<HexFrame> allFrames = HexFrame.findAllFrames();
                    if (!allFrames.isEmpty()) {
                        finalWindowMenu.addSeparator();
                    }
                    for (HexFrame frame : allFrames) {
                        finalWindowMenu.add(new ActivateWindowMenuItem(frame));
                    }
                }
            });
        }

        //XXX: Maybe the repo should be in the list returned from this... I'm not really sure.
        List<Path> scriptsDirs = Platform.getCurrent().getScriptsDirs().stream()
            .map(File::toPath)
            .collect(Collectors.toList());
        scriptsDirs.add(Repository.getRoot());
        ScriptMenu scriptsMenu = new ScriptMenu(Resources.getString("Scripts.name"), scriptsDirs);
        scriptsMenu.addSeparator();
        scriptsMenu.add(new OpenScriptDirectoryAction());
        scriptsMenu.useCurrentItemsAsStaticItems();

        JMenu helpMenu = new JMenu(Resources.getString("Help.name"));
        // TODO: Help / User Guide
        // TODO: Help / Scripting Guide
        helpMenu.add(new OpenSampleNotebookAction(application));
        if (!MacFactory.isMac()) {
            helpMenu.add(new AboutAction());
        }

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        if (windowMenu != null) {
            menuBar.add(windowMenu);
        }
        menuBar.add(scriptsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }
}
