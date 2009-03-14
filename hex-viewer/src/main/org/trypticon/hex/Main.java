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

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.trypticon.binary.Binary;
import org.trypticon.binary.BinaryFactory;

/**
 * Main entry point.
 *
 * @author trejkaz
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new Main().execute(args);
    }

    public void execute(Object[] args) throws Exception {
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

        if (args.length == 1 && args[0] instanceof String) {
            File file = new File((String) args[0]);
            frame.loadFile(file);
        } else {
            frame.loadBinary(loadSample());
        }
    }

    private Binary loadSample() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        InputStream stream = getClass().getClassLoader().getResourceAsStream("org/trypticon/hex/Sample.class");
        try {
            byte[] buf = new byte[16*1024];
            int bytesRead;
            while ((bytesRead = stream.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
        } finally {
            stream.close();
        }

        return BinaryFactory.wrap(out.toByteArray());
    }
}
