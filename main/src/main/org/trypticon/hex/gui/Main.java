/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2010  Trejkaz, Hex Project
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

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.trypticon.hex.gui.notebook.Notebook;
import org.trypticon.hex.gui.notebook.NotebookStorage;
import org.trypticon.hex.gui.prefs.WorkspaceStateTracker;
import org.trypticon.hex.gui.sample.OpenSampleNotebookAction;
import org.trypticon.hex.gui.warmup.JRubyWarmup;
import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Main entry point.
 *
 * @author trejkaz
 */
public class Main {
    public static void main(final String[] args) throws Exception {

        new JRubyWarmup().start();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Main().execute(args);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    // TODO: Generic error dialog.  Need a utility for this.
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void execute(String[] args) throws Exception {
        HexApplication application = new HexApplication();

        application.openInitialWindows();

        if (args.length > 0) {
            for (String arg : args) {
                // TODO: Support a URL here too.
                // TODO: Support binary here too. Find a way to distinguish this in this context.
                application.openNotebook(Paths.get(arg));
            }
        }
    }
}
