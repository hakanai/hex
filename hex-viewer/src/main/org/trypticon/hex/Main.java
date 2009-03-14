/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
