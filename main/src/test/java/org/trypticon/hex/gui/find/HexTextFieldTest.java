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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Tests for {@link HexTextField}.
 *
 * @author trejkaz
 */
@SuppressWarnings("HardCodedStringLiteral")
public class HexTextFieldTest {
    private HexTextField textField;

    public static Object[][] parameters() {
        return new Object[][] {
            // Single character typing at the end.
            { "|", "a", "A[0] " },
            { "A[0] ", "b", "AB |" },
            { "AB |", "c", "AB C[0] " },
            { "AB C[0] ", "d", "AB CD |" },

            // Single character typing from the middle of a byte.
            { "A|D ", "b", "AB [0]D " },
            { "AB [0]D ", "c", "AB C|D " },

            // Multiple character inserts from the end of a byte.
            { "AB |", "cd", "AB CD |"},
            { "AB |", "cd ", "AB CD |"},
            { "AB |", " cd", "AB CD |"},

            // Multiple character inserts from the end of a byte with leftover nibbles.
            { "AB |", "cd e", "AB CD E[0] "},
            { "AB |", "cde", "AB CD E[0] "},
            { "AB| ", "cde", "AB CD E[0] "},

            // Multiple character inserts from the middle of a byte.
            { "A|B ", "cd", "AC D|B "},
            { "A|B ", "cde", "AC DE [0]B "},

            // Single character deletes.
            { "[A]B ", "", "|" },
            { "A[B] ", "", "|" },
            { "AB[ ]", "", "|" },

            // Range deletes of whole bytes.
            { "AB [CD] EF ", "", "AB |EF "},
            { "AB [CD ]EF ", "", "AB |EF "},
            { "AB[ CD] EF ", "", "AB |EF "},

            // Range deletes which eat the whole start or end byte.
            { "AB [CD E]F ", "", "AB [0]F "},
            { "A[B CD] EF ", "", "A[0] EF "},

            // Range delete from middle to middle.
            { "A[B CD E]F ", "", "A|F " },

            // Replaces of whole bytes.
            { "AB [12] EF ", "cd", "AB CD |EF "},
            { "AB [12] EF ", "cd ", "AB CD |EF "},

            // Range replace from middle to middle.
            { "A[1 23 4]F ", "bc de", "AB CD E|F " },

            //XXX: This would be good to improve but seems hard because of how we're handling non-hex chars.
//            { "A[0] ", "g", "A[0] " },
        };
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void test(String startState, String typed, String expectedEndState) {
        setupInitialState(startState);

        textField.replaceSelection(typed);

        checkEndState(expectedEndState);
    }

    private void setupInitialState(String startState) {
        // Initial state
        int caretPosition = startState.indexOf('|');
        int selectionStart = startState.indexOf('[');
        int selectionEnd = startState.indexOf(']');
        String text = startState.replace("|", "").replace("[", "").replace("]", "");
        assertThat(text.length() % 3, is(0)); // testing the test
        textField = new HexTextField(text, 40);
        if (caretPosition >= 0) {
            textField.setCaretPosition(caretPosition);
        } else if (selectionStart >= 0 && selectionEnd >= 0) {
            textField.select(selectionStart, selectionEnd - 1);
        }
    }

    private void checkEndState(String expectedEndState) {
        String text = textField.getText();
        int selectionStart = textField.getSelectionStart();
        int selectionEnd = textField.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            text = text.substring(0, selectionStart) + '|' + text.substring(selectionStart);
        } else {
            text = text.substring(0, selectionStart) + '[' +
                   text.substring(selectionStart, selectionEnd) + ']' +
                   text.substring(selectionEnd);
        }
        assertThat(text, is(equalTo(expectedEndState)));
    }
}
