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

package org.trypticon.hex.scripting;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.ActionMap;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RecordableTextAction;
import org.jetbrains.annotations.NonNls;

/**
 * Action map with missing actions inserted.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class RTAMacActionMap extends ActionMap {
    @NonNls
    public static final String rtaScrollBeginAction = "RTA.ScrollBeginAction";
    @NonNls
    public static final String rtaScrollEndAction = "RTA.ScrollEndAction";

    public RTAMacActionMap(ActionMap delegate) {
        setParent(delegate);

        put(rtaScrollBeginAction, new ScrollBeginAction());
        put(rtaScrollEndAction, new ScrollEndAction());
    }

    private static class ScrollBeginAction extends RecordableTextAction {
        private ScrollBeginAction() {
            super(rtaScrollBeginAction);
        }

        @Override
        public void actionPerformedImpl(ActionEvent event, RTextArea textArea) {
            try {
                Rectangle2D view = textArea.modelToView2D(0);
                if (view != null) {
                    textArea.scrollRectToVisible(new Rectangle((int) view.getX(), (int) view.getY(),
                                                               (int) view.getWidth(), (int) view.getHeight()));
                }
            } catch (BadLocationException e) {
                // Just ignore it, I guess.
            }
        }

        @Override
        public final String getMacroID() {
            return rtaScrollBeginAction;
        }

    }

    private static class ScrollEndAction extends RecordableTextAction {
        private ScrollEndAction() {
            super(rtaScrollEndAction);
        }

        @Override
        public void actionPerformedImpl(ActionEvent event, RTextArea textArea) {
            try {
                Rectangle2D view = textArea.modelToView2D(textArea.getDocument().getLength() - 1);
                if (view != null) {
                    textArea.scrollRectToVisible(new Rectangle((int) view.getX(), (int) view.getY(),
                                                               (int) view.getWidth(), (int) view.getHeight()));
                }
            } catch (BadLocationException e) {
                // Just ignore it, I guess.
            }
        }

        @Override
        public String getMacroID() {
            return rtaScrollEndAction;
        }
    }

}
