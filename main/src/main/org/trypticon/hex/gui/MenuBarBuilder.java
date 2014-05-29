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

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.trypticon.gum.MacFactory;
import org.trypticon.hex.formats.ruby.RubyStructureDSL;
import org.trypticon.hex.gui.formats.DropStructureAction;
import org.trypticon.hex.gui.prefs.PreferredDirectoryManager;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.gui.util.DelegatingAction;

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
        PreferredDirectoryManager preferredDirectoryManager = new PreferredDirectoryManager();

        JMenu fileMenu = new JMenu(Resources.getString(MacFactory.isMac() ? "File.name.mac" : "File.name.other"));
        fileMenu.add(new NewNotebookAction(application, preferredDirectoryManager));
        fileMenu.add(new OpenNotebookAction(application, preferredDirectoryManager));
        // TODO: Open Recent

        fileMenu.addSeparator();
        fileMenu.add(new CloseNotebookAction());
        Action saveAction = new SaveNotebookAction(preferredDirectoryManager, false);
        if (frame != null) {
            frame.getRootPane().getActionMap().put("save", saveAction);
        }
        fileMenu.add(saveAction);
        fileMenu.add(new SaveNotebookAction(preferredDirectoryManager, true));
        // TODO: Revert to Saved

        if (!MacFactory.isMac()) {
            fileMenu.addSeparator();
            fileMenu.add(new ExitAction(application));
        }

        // TODO: Copy as:
        //  - hex
        //  - java source
        //  - ?
        JMenu editMenu = new JMenu(Resources.getString("Edit.name"));

        UndoHelper undoHelper = application.getUndoHelper();
        editMenu.add(undoHelper.getUndoAction());
        editMenu.add(undoHelper.getRedoAction());

        editMenu.addSeparator();
        editMenu.add(new DelegatingAction("Copy", "copy-to-clipboard"));
        editMenu.add(new DeleteAction());
        editMenu.add(new DelegatingAction("SelectAll", "select-all"));

        editMenu.addSeparator();
        editMenu.add(new AddAnnotationAction());
        editMenu.add(new AddSubRegionAction());

        // Temporarily disabled until more things are sorted out.
        //editMenu.addSeparator();
        //editMenu.add(buildFormatsMenu());

        JMenu helpMenu = new JMenu(Resources.getString("Help.name"));
        // TODO: Help / User Guide
        helpMenu.add(new OpenSampleNotebookAction(application));
        // TODO: Help / About (non-Mac only.  Mac needs to hook into the app menu.)

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
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
