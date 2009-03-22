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

package org.trypticon.hex.util.swingsupport;

import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Window;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * A convenience action class with some facility for catching errors.
 *
 * @author trejkaz
 */
public abstract class BaseAction extends AbstractAction {
    private static final Logger logger = Logger.getLogger(BaseAction.class.getName());

    public void actionPerformed(ActionEvent event) {
        try {
            doAction(event);
        } catch (ActionException e) {
            JOptionPane.showMessageDialog(findSourceWindow((Component) event.getSource()), e.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Unexpected error in UI action", t);

            JOptionPane.showMessageDialog(findSourceWindow((Component) event.getSource()),
                                          "Unexpected error in UI action.  Check the log for further details.",
                                          "Unexpected Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Window findSourceWindow(Component component) {
        if (component instanceof Window) {
            return (Window) component;
        } else {
            return SwingUtilities.getWindowAncestor(component);
        }
    }

    protected abstract void doAction(ActionEvent event) throws Exception;
}
