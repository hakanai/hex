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

import java.util.Locale;

import org.trypticon.hex.util.Format;
import org.trypticon.hex.util.Localisable;

/**
 * Base class for localisable exceptions.
 *
 * @author trejkaz
 */
public class LocalisableException extends Exception {
    private static final long serialVersionUID = 8330429681909770558L;

    private final Localisable message;

    public LocalisableException(Localisable message) {
        super(message.toLocalisedString(Format.LONG));
        this.message = message;
    }

    public LocalisableException(Localisable message, Throwable cause) {
        super(message.toLocalisedString(Format.LONG), cause);
        this.message = message;
    }

    public Localisable getMessageObject() {
        return message;
    }

    @Override
    public String getMessage() {
        return message.toLocalisedString(Format.LONG, Locale.ROOT);
    }

    @Override
    public String getLocalizedMessage() {
        return message.toLocalisedString(Format.LONG);
    }
}
