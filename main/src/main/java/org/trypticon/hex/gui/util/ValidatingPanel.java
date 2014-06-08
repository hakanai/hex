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

import javax.swing.JPanel;

/**
 * A panel which can validate its own input.
 *
 * @author trejkaz
 */
public abstract class ValidatingPanel extends JPanel {
    private boolean inputValid;

    /**
     * Call this method any time a change in the input might change the validity.
     */
    protected void updateInputValid() {
        setInputValid(validateInput());
    }

    /**
     * Called to test if the input on the panel is valid.
     *
     * @return {@code true} if the input is valid, {@code false} otherwise.
     */
    protected abstract boolean validateInput();

    /**
     * Tests if the input on the panel is valid.
     * This is a JavaBeans bound property.
     *
     * @return {@code true} if the input is valid, {@code false} otherwise.
     */
    public boolean isInputValid() {
        return inputValid;
    }

    /**
     * Sets the input as valid or invalid.
     *
     * @param inputValid the new state.
     */
    private void setInputValid(boolean inputValid) {
        boolean oldInputValid = this.inputValid;
        this.inputValid = inputValid;
        firePropertyChange("inputValid", oldInputValid, inputValid);
    }
}
