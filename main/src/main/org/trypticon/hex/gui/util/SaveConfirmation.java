/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2012  Trejkaz, Hex Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.trypticon.hex.gui.util;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.trypticon.hex.util.swingsupport.PLAFUtils;

/**
 * Support for showing a confirmation dialog for saving the current document.
 *
 * @author trejkaz
 */
public abstract class SaveConfirmation {
    /**
     * Gets an instance of the save confirmation utility appropriate for the current Look and Feel.
     *
     * @return a new save confirmation support object.
     */
    public static SaveConfirmation getInstance() {
        if (PLAFUtils.isQuaqua()) {
            return new QuaquaSaveConfirmation();
        } else {
            return new DefaultSaveConfirmation();
        }
    }

    /**
     * Shows the confirmation pane.
     *
     * @param owner a component relative to which this will be displayed.  Generally a root pane.
     * @param callback a callback which is called with the option the user chose.
     */
    public void show(Component owner, final Callback<Option> callback) {
        // XXX: It might be OK to put the name of the pane into the message, but
        // other apps don't appear to do this.
        String optionPaneCss = UIManager.getString("OptionPane.css");
        if (optionPaneCss == null) {
            optionPaneCss = "";
        }
        String message = "<html>" + optionPaneCss +
                         "<b>Do you want to save changes to this document<br>before closing?</b><p>" +
                         "If you don't save, your changes will be lost.";

        JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);

        Option[] options = { Option.SAVE, Option.CANCEL, Option.DO_NOT_SAVE };
        optionPane.setOptions(options);
        optionPane.setInitialValue(options[0]);
        optionPane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);

        showOptionPane(owner, optionPane, new Callback<Object>() {
            @Override
            public void execute(Object option) {
                if (option instanceof Option) {
                    callback.execute((Option) option);
                } else {
                    callback.execute(Option.CANCEL);
                }
            }
        });
    }

    /**
     * Shows the option pane.  This may be done in different ways as appropriate for the
     * current Look and Feel.
     *
     * @param owner a component relative to which this will be displayed.  Generally a root pane.
     * @param optionPane the option pane to display.
     * @param callback a callback which receives the option selected by the user.  May be {@code null}
     * or not an {@link Option} object, in which case it is treated as if the user selected
     * to cancel.
     */
    protected abstract void showOptionPane(Component owner, JOptionPane optionPane, Callback<Object> callback);

    /**
     * Results of showing the dialog.
     */
    public static enum Option {
        CANCEL("Cancel"),

        SAVE("Save"),

        DO_NOT_SAVE("Don't Save");

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
