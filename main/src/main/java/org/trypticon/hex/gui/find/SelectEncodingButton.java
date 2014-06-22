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

package org.trypticon.hex.gui.find;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;

/**
 * A button showing the selected encoding. Clicking the button pops up the selection dialog.
 *
 * @author trejkaz
 */
class SelectEncodingButton extends JButton {
    private Charset encoding;

    SelectEncodingButton() {
        setEncoding(StandardCharsets.UTF_8);
        addActionListener(event -> {
            SelectEncodingPane pane = new SelectEncodingPane();
            Charset selected = pane.showDialog(getRootPane());
            if (selected != null) {
                setEncoding(selected);
            }
        });
    }

    /**
     * Gets the current encoding.
     *
     * @return the encoding.
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     * Sets a new encoding.
     *
     * @param encoding the encoding.
     */
    public void setEncoding(Charset encoding) {
        setText(encoding.name());
        Charset oldEncoding = this.encoding;
        this.encoding = encoding;
        firePropertyChange("encoding", oldEncoding, encoding);
    }
}
