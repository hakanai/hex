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

package org.trypticon.hex.gui.file;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.trypticon.hex.gui.Resources;
import org.trypticon.hex.gui.util.Callback;
import org.trypticon.hex.gui.util.OptionPaneDisplayer;

/**
 * Displays the appropriate confirmation for a Revert to Saved operation.
 *
 * @author trejkaz
 */
public class RevertToSavedConfirmation {
    /**
     * Shows the confirmation pane.
     *
     * @param owner a component relative to which this will be displayed.  Generally a root pane.
     * @param documentName the name of the document.
     * @param callback a callback which is called with the option the user chose.
     */
    public void show(Component owner, String documentName, final Callback<Option> callback) {
        //TODO: Unify code which is similar to SaveConfirmation.

        // XXX: It might be OK to put the name of the pane into the message, but
        // other apps don't appear to do this.
        String optionPaneCss = UIManager.getString("OptionPane.css");
        if (optionPaneCss == null) {
            optionPaneCss = "";
        }
        String message = Resources.getString("RevertToSaved.confirmationMessage", optionPaneCss, documentName);
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);

        Option[] options = { Option.REVERT, Option.CANCEL };
        optionPane.setOptions(options);
        optionPane.setInitialValue(options[0]);

        OptionPaneDisplayer.getInstance().showOptionPane(
            owner, Resources.getString("RevertToSaved.name"), optionPane, option -> {
            if (option instanceof Option) {
                callback.execute((Option) option);
            } else {
                callback.execute(Option.CANCEL);
            }
        });
    }

    /**
     * Results of showing the dialog.
     */
    public static enum Option {
        CANCEL(Resources.getString("RevertToSaved.confirmation.cancel")),

        REVERT(Resources.getString("RevertToSaved.confirmation.revert"));

        private String display;

        private Option(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }
}
