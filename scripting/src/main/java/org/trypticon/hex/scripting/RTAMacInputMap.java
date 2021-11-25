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

import java.awt.event.KeyEvent;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

import org.fife.ui.rtextarea.RTextAreaEditorKit;

/**
 * Input map for Mac OS X.
 *
 * @author trejkaz
 */
// Swing's own guidelines say not to use serialisation.
@SuppressWarnings("serial")
public class RTAMacInputMap extends InputMap {
    public RTAMacInputMap() {

        int shift = KeyEvent.SHIFT_DOWN_MASK;
        int alt = KeyEvent.ALT_DOWN_MASK;
        int cmd = KeyEvent.META_DOWN_MASK;

        //TODO: Various mappings have no action key to map to.

        put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME,   0),             RTAMacActionMap.rtaScrollBeginAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME,   shift),         DefaultEditorKit.selectionBeginAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_END,    0),             RTAMacActionMap.rtaScrollEndAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_END,    shift),         DefaultEditorKit.selectionEndAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,   0),             DefaultEditorKit.backwardAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,   shift),         DefaultEditorKit.selectionBackwardAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,   alt),           DefaultEditorKit.previousWordAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,   alt|shift),     DefaultEditorKit.selectionPreviousWordAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,   cmd),           DefaultEditorKit.beginLineAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,   cmd|shift),     DefaultEditorKit.selectionBeginLineAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,   0),             DefaultEditorKit.downAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,   shift),         DefaultEditorKit.selectionDownAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,   cmd),           DefaultEditorKit.endAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,   cmd|shift),     DefaultEditorKit.selectionEndAction);
//        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,   alt),           /* to next end-line */);
//        put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,   alt|shift),     /* select to next end-line */);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,  0),             DefaultEditorKit.forwardAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,  shift),         DefaultEditorKit.selectionForwardAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,  alt),           DefaultEditorKit.nextWordAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,  alt|shift),     DefaultEditorKit.selectionNextWordAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,  cmd),           DefaultEditorKit.endLineAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,  cmd|shift),     DefaultEditorKit.selectionEndLineAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,     0),             DefaultEditorKit.upAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,     shift),         DefaultEditorKit.selectionUpAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,     cmd),           DefaultEditorKit.beginAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,     cmd|shift),     DefaultEditorKit.selectionBeginAction);
//        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,     alt),           /* to next start-line */);
//        put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,     alt|shift),     /* select to next start-line */);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,0),             DefaultEditorKit.pageUpAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP,shift),         RTextAreaEditorKit.rtaSelectionPageUpAction);
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaSelectionPageLeftAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,0),           DefaultEditorKit.pageDownAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN,shift),       RTextAreaEditorKit.rtaSelectionPageDownAction);
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaSelectionPageRightAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_CUT,    0),             DefaultEditorKit.cutAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_COPY,   0),             DefaultEditorKit.copyAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE,  0),             DefaultEditorKit.pasteAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_X,      cmd),           DefaultEditorKit.cutAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_C,      cmd),           DefaultEditorKit.copyAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_V,      cmd),           DefaultEditorKit.pasteAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),             DefaultEditorKit.deleteNextCharAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, alt),           DefaultEditorKit.deleteNextWordAction);
        // This one doesn't actually exist in native apps but is consistent with cmd+bksp
        put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, cmd),           RTextAreaEditorKit.rtaDeleteRestOfLineAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_A,      cmd),           DefaultEditorKit.selectAllAction);

//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaDeleteLineAction);
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaJoinLinesAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),         DefaultEditorKit.deletePrevCharAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, shift),     DefaultEditorKit.deletePrevCharAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, alt),       RTextAreaEditorKit.rtaDeletePrevWordAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, cmd),       RTextAreaEditorKit.rtaDeleteRestOfLineAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB,    0),             DefaultEditorKit.insertTabAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,  0),             DefaultEditorKit.insertBreakAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,  shift),         DefaultEditorKit.insertBreakAction);
        //XXX: Xcode uses Ctrl+Space for this, but Ctrl+Space is also a global shortcut.
        put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,  cmd),           RTextAreaEditorKit.rtaDumbCompleteWordAction);

        put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,      cmd),           RTextAreaEditorKit.rtaUndoAction);
        put(KeyStroke.getKeyStroke(KeyEvent.VK_Z,      cmd|shift),     RTextAreaEditorKit.rtaRedoAction);

        // apps shouldn't use F keys
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaNextBookmarkAction);
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaPrevBookmarkAction);
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaToggleBookmarkAction);

//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaPrevOccurrenceAction);
//        put(/* ??? */,                                                 RTextAreaEditorKit.rtaNextOccurrenceAction);


    }
}
