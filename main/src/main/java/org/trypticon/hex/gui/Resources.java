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

package org.trypticon.hex.gui;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.jetbrains.annotations.NonNls;

import org.trypticon.gum.MacFactory;
import org.trypticon.hex.util.Format;
import org.trypticon.hex.util.Localisable;
import org.trypticon.hex.util.swingsupport.GuiLocale;

/**
 * Utilities for dealing with resources.
 *
 * @author trejkaz
 */
public class Resources {
    @NonNls
    private static final String NAME_SUFFIX = ".name";

    @NonNls
    private static final String MNEMONIC_SUFFIX = ".mnemonic";

    @NonNls
    private static final String MAC_ACCELERATOR_SUFFIX = ".accelerator.mac";

    @NonNls
    private static final String OTHER_ACCELERATOR_SUFFIX = ".accelerator.other";

    private Resources() {
    }

    private static ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("org/trypticon/hex/gui/Bundle", locale);
    }

    public static String getString(@NonNls String key) {
        return getString(GuiLocale.get(), key);
    }

    public static String getString(Locale locale, @NonNls String key) {
        return getBundle(locale).getString(key);
    }

    public static String getString(@NonNls String key, Object... params) {
        return getString(GuiLocale.get(), key, params);
    }

    public static String getString(Locale locale, @NonNls String key, Object... params) {
        return new MessageFormat(getString(locale, key), locale).format(params);
    }

    public static Localisable getMessage(@NonNls String key) {
        return new Message(key);
    }

    public static void localiseAction(Action action, @NonNls String baseKey) {
        try {
            action.putValue(Action.NAME, getString(baseKey + NAME_SUFFIX));
        } catch (MissingResourceException e) {
            // Not a problem.
        }
        try {
            action.putValue(Action.MNEMONIC_KEY, (int) getString(baseKey + MNEMONIC_SUFFIX).charAt(0));
        } catch (MissingResourceException e) {
            // Not a problem.
        }
        try {
            String acceleratorSuffix = MacFactory.isMac() ? MAC_ACCELERATOR_SUFFIX : OTHER_ACCELERATOR_SUFFIX;
            action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(getString(baseKey + acceleratorSuffix)));
        } catch (MissingResourceException e) {
            // Not a problem.
        }
    }

    private static class Message implements Localisable {
        @NonNls
        private final String key;

        private final Object[] params;

        public Message(@NonNls String key, Object... params) {
            this.key = key;
            this.params = Arrays.copyOf(params, params.length);
        }

        @Override
        public String toLocalisedString(Format format) {
            return getString(key, params);
        }

        @Override
        public String toLocalisedString(Format format, Locale locale) {
            return getString(locale, key, params);
        }
    }

}
