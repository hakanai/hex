/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009  Trejkaz, Hex Project
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

import net.roydesign.app.Application;

/**
 * <p>Mac-specific look and feel fixes.</p>
 *
 * <p>This class refers to some classes which are only distributed with the Mac
 *    version, which is why it has been separated out from the main initialisation
 *    stuff (it may speed up startup time too, by decreasing the size of the
 *    initialising classes.</p>
 *
 * @author trejkaz
 */
public class MacPLAFBootstrap {
    private static final Logger logger = Logger.getLogger(MacPLAFBootstrap.class.getName());

    /**
     * Initialises look and feel for Aqua (Mac OS X.)
     *
     * @throws Exception if an error occurs.
     */
    static void init() throws Exception {
        // Look and feel tweaks for Apple's runtime.
        // These need to be done before setting the LAF.

        Application application = Application.getInstance();
        application.setName("Hex");
        // TODO: MRJAdapter is supposed to do this but setName doesn't appear to work.
        // Workaround is to keep using the system property.
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hex");

        // TODO: About (along with Help -> About in main app.)
        // TODO: Preferences - when we get some. ;-)
        application.getQuitJMenuItem().addActionListener(new ExitAction());

        // When frames are visible this system property will make that menu become the screen menu.
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // And then a different menu when there are no frames visible:
        application.setFramelessJMenuBar(HexFrame.buildMenuBar(null));

        try {
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unexpected error initialising Quaqua, falling back to Aqua", e);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
    }
}
