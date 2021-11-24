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

package org.trypticon.hex.gui.datatransfer;

import java.awt.event.ActionEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;

import org.jetbrains.annotations.NonNls;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.FocusedComponentAction;

/**
 * Common base class for Cut and Copy, since they share so much in common.
 *
 * @author trejkaz
 */
abstract class CutOrCopyAction extends FocusedComponentAction {
    private final int requiredActionMask;
    private final Action delegateAction;

    @Nullable
    private SelectionEvaluator selectionEvaluator;

    CutOrCopyAction(@NonNls String baseKey, int requiredActionMask, Action delegateAction) {
        Resources.localiseAction(this, baseKey);
        this.requiredActionMask = requiredActionMask;
        this.delegateAction = delegateAction;
    }

    @Override
    protected boolean shouldBeEnabled(@Nonnull JComponent focusOwner) {
        TransferHandler transferHandler = focusOwner.getTransferHandler();
        if (transferHandler == null) {
            return false;
        }

        if (selectionEvaluator == null || selectionEvaluator.component != focusOwner) {
            if (selectionEvaluator != null) {
                selectionEvaluator.detach();
            }
            if (focusOwner instanceof JTextComponent) {
                selectionEvaluator = new TextSelectionEvaluator((JTextComponent) focusOwner);
            } else if (focusOwner instanceof JTable) {
                selectionEvaluator = new TableSelectionEvaluator((JTable) focusOwner);
            }
        }

        if (selectionEvaluator != null && !selectionEvaluator.isSomethingSelected()) {
            return false;
        }

        int sourceActions = transferHandler.getSourceActions(focusOwner);
        return (sourceActions & requiredActionMask) != 0;
    }

    @Override
    protected void doAction(@Nonnull JComponent focusOwner) throws Exception {
        delegateAction.actionPerformed(new ActionEvent(
            focusOwner, ActionEvent.ACTION_PERFORMED, (String) delegateAction.getValue(Action.NAME)));
    }

    private abstract class SelectionEvaluator {
        private final JComponent component;

        protected SelectionEvaluator(JComponent component) {
            this.component = component;
        }

        abstract boolean isSomethingSelected();

        abstract void detach();
    }

    private class TextSelectionEvaluator extends SelectionEvaluator implements CaretListener {
        private final JTextComponent text;

        public TextSelectionEvaluator(JTextComponent text) {
            super(text);
            this.text = text;
            text.addCaretListener(this);
        }

        @Override
        boolean isSomethingSelected() {
            return text.getSelectionStart() != text.getSelectionEnd();
        }

        @Override
        void detach() {
            text.removeCaretListener(this);
        }

        @Override
        public void caretUpdate(CaretEvent event) {
            updateEnabled();
        }
    }

    private class TableSelectionEvaluator extends SelectionEvaluator implements ListSelectionListener {
        private final JTable table;

        private TableSelectionEvaluator(JTable table) {
            super(table);
            this.table = table;
            table.getSelectionModel().addListSelectionListener(this);
        }

        @Override
        boolean isSomethingSelected() {
            return table.getSelectionModel().getMinSelectionIndex() >= 0;
        }

        @Override
        public void detach() {
            table.getSelectionModel().removeListSelectionListener(this);
        }

        @Override
        public void valueChanged(ListSelectionEvent event) {
            updateEnabled();
        }
    }
}
