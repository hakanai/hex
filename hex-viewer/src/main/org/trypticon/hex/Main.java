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

package org.trypticon.hex;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Main entry point.
 *
 * @author trejkaz
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // Look and feel tweaks for Apple's runtime.
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hex");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // For whatever reason, Windows' Java doesn't set this automatically.
        // And it seems it has to be done after setting Apple's properties, as they
        // look at the properties on startup.
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        HexFrame frame = new HexFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
