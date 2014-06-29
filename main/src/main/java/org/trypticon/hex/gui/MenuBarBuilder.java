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

import java.util.List;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;

import org.trypticon.gum.MacFactory;
import org.trypticon.hex.formats.ruby.RubyStructureDSL;
import org.trypticon.hex.gui.datatransfer.CopyAction;
import org.trypticon.hex.gui.datatransfer.CutAction;
import org.trypticon.hex.gui.datatransfer.PasteAction;
import org.trypticon.hex.gui.file.CloseNotebookAction;
import org.trypticon.hex.gui.file.NewNotebookAction;
import org.trypticon.hex.gui.file.OpenNotebookAction;
import org.trypticon.hex.gui.file.RevertToSavedAction;
import org.trypticon.hex.gui.file.SaveNotebookAction;
import org.trypticon.hex.gui.find.FindNextAction;
import org.trypticon.hex.gui.find.FindPreviousAction;
import org.trypticon.hex.gui.find.JumpToOffsetAction;
import org.trypticon.hex.gui.find.JumpToSelectionAction;
import org.trypticon.hex.gui.find.ShowFindBarAction;
import org.trypticon.hex.gui.formats.DropStructureAction;
import org.trypticon.hex.gui.recent.OpenRecentMenu;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.gui.undo.GlobalUndoHelper;
import org.trypticon.hex.gui.util.DelegatingAction;
import org.trypticon.hex.gui.util.MenuAdapter;

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
        JMenu findMenu = new JMenu(Resources.getString("FindMenu.name"));
        findMenu.add(new ShowFindBarAction());
        findMenu.add(new FindNextAction());
        findMenu.add(new FindPreviousAction());
        findMenu.add(new JumpToSelectionAction());
        findMenu.add(new JumpToOffsetAction());
        editMenu.add(findMenu);

        editMenu.addSeparator();
        editMenu.add(new AddAnnotationAction());
        editMenu.add(new AddSubRegionAction());

        // Temporarily disabled until more things are sorted out.
        //editMenu.addSeparator();
        //editMenu.add(buildFormatsMenu());

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

        JMenu helpMenu = new JMenu(Resources.getString("Help.name"));
        // TODO: Help / User Guide
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
        menuBar.add(helpMenu);
        return menuBar;
    }

    /**
     * Builds the formats menu.
     *
     * @return the formats menu.
     */
    private JMenu buildFormatsMenu() {
        JMenu menu = new JMenu(Resources.getString("Formats.name"));
        menu.add(new DropStructureAction(RubyStructureDSL.load(SingleHexFrame.class.getResource("/org/trypticon/hex/formats/classfile/class_file.rb"))));
        menu.addSeparator();
        menu.add(new DropStructureAction(RubyStructureDSL.load(SingleHexFrame.class.getResource("/org/trypticon/hex/formats/gif/gif_header.rb"))));
        menu.addSeparator();
        menu.add(new DropStructureAction(RubyStructureDSL.load(SingleHexFrame.class.getResource("/org/trypticon/hex/formats/jpeg/jpeg_image.rb"))));
        menu.add(new DropStructureAction(RubyStructureDSL.load(SingleHexFrame.class.getResource("/org/trypticon/hex/formats/jpeg/jpeg_eoi.rb"))));
        return menu;
    }

}
