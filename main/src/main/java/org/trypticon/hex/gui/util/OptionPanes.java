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

import java.awt.Component;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Convenience methods for showing option panes.
 *
 * @author trejkaz
 */
public class OptionPanes {

    /**
     * Shows a validating input dialog.
     *
     * @param parentComponent the parent component for the dialog.
     * @param validatingPanel the panel to show.
     * @param initiallyFocusedComponent a component to give initial focus.
     * @param dialogTitle the title for the dialog.
     * @param affirmativeVerb the localised verb for the affirmative button.
     * @param negativeVerb the localised verb for the negative button.
     * @return {@code true} if the dialog was confirmed (which implies it was also valid.)
     */
    public static boolean showInputDialog(Component parentComponent,
                                          ValidatingPanel validatingPanel,
                                          JComponent initiallyFocusedComponent,
                                          String dialogTitle,
                                          String affirmativeVerb,
                                          String negativeVerb) {

        initiallyFocusedComponent.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                JComponent component = event.getComponent();
                component.requestFocusInWindow();
                component.removeAncestorListener( this );
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        JButton affirmativeButton = new JButton(affirmativeVerb);
        JButton negativeButton = new JButton(negativeVerb);

        PropertyChangeListener listener = (event) ->
            affirmativeButton.setEnabled((boolean) event.getNewValue());

        validatingPanel.addPropertyChangeListener("inputValid", listener);
        try {
            Object[] buttons = new Object[] { affirmativeButton, negativeButton };
            JOptionPane optionPane = new JOptionPane(validatingPanel, JOptionPane.PLAIN_MESSAGE,
                                                     JOptionPane.DEFAULT_OPTION, null,
                                                     buttons, affirmativeButton);

            affirmativeButton.addActionListener((event) -> optionPane.setValue(0));
            negativeButton.addActionListener((event) -> optionPane.setValue(1));

            JDialog dialog = optionPane.createDialog(parentComponent, dialogTitle);
            dialog.setVisible(true);
            Object selectedValue = optionPane.getValue();
            return selectedValue instanceof Integer && (int) selectedValue == 0;
        } finally {
            validatingPanel.removePropertyChangeListener("inputValid", listener);
        }
    }
}
