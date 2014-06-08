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

package org.trypticon.hex.gui.util;

import java.awt.Window;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * @author trejkaz
 */
public class SheetHack {
    /**
     * If running on Aqua look and feel, makes the given dialog a sheet.
     *
     * @param dialog the dialog.
     */
    public static void makeSheet(JDialog dialog) {
        //HACK: Set the window to display as a sheet.
        if (PLAFUtils.isAqua()) {

            // This hack *should* work, but doesn't, and I can't figure out why.
//            dialog.removeNotify();
//            dialog.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
//            dialog.getRootPane().putClientProperty("apple.awt.documentModalSheet", Boolean.TRUE);
//            dialog.addNotify();

            dialog.pack();
            ComponentPeer peer = dialog.getPeer();

            // File dialogs are CFileDialog instead. Unfortunately this means this hack can't work for those. :(
            if ("sun.lwawt.LWWindowPeer".equals(peer.getClass().getName())) {
                try {
                    Method getPlatformWindowMethod = peer.getClass().getMethod("getPlatformWindow");
                    Object platformWindow = getPlatformWindowMethod.invoke(peer);

                    if ("sun.lwawt.macosx.CPlatformWindow".equals(platformWindow.getClass().getName())) {
                        Method setStyleBitsMethod = platformWindow.getClass().getDeclaredMethod("setStyleBits", int.class, boolean.class);
                        setStyleBitsMethod.setAccessible(true);
                        setStyleBitsMethod.invoke(platformWindow, 64 /* CPlatformWindow.SHEET */, true);
                    }

                    Window parent = dialog.getOwner();
                    dialog.setLocation(dialog.getLocation().x, parent.getLocation().y + parent.getInsets().top);
                } catch (Exception e) {
                    Logger.getLogger(SheetHack.class.getName())
                        .log(Level.WARNING, "Couldn't call setStyleBits", e);
                }
            }
        }
    }
}
