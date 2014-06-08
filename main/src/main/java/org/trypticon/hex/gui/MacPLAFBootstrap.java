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

import java.util.logging.Level;
import javax.swing.UIManager;

import org.trypticon.gum.MacFactory;
import org.trypticon.gum.eawt.Application;
import org.trypticon.hex.util.LoggerUtils;

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

    /**
     * Initialises look and feel for Aqua (Mac OS X.)
     *
     * @param application the main application.
     * @throws Exception if an error occurs.
     */
    void init(final HexApplication application) throws Exception {
        // Look and feel tweaks for Apple's runtime.
        // These need to be done before setting the LAF.

        // When frames are visible this system property will make that menu become the screen menu.
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        try {
            UIManager.setLookAndFeel("org.trypticon.haqua.HaquaLookAndFeel");
        } catch (Exception e) {
            LoggerUtils.get().log(Level.WARNING, "Unexpected error initialising Haqua, falling back to Aqua", e);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }

        final Application macApplication = MacFactory.getApplication();

        // TODO: About (along with Help -> About in main app.)
        // TODO: Preferences - when we get some. ;-)
        macApplication.setQuitHandler((quitEvent, quitResponse) -> application.tryToExit(okToExit -> {
            if (okToExit) {
                quitResponse.performQuit();
            } else {
                quitResponse.cancelQuit();
            }
        }));

        macApplication.setDefaultMenuBar(new MenuBarBuilder(application).buildMenuBar(null));
    }
}
