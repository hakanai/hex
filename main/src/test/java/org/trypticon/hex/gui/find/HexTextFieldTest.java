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

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link HexTextField}.
 *
 * @author trejkaz
 */
@RunWith(Parameterized.class)
@SuppressWarnings("HardCodedStringLiteral")
public class HexTextFieldTest {
    private final String startState;
    private final String typed;
    private final String expectedEndState;

    HexTextField textField;

    public HexTextFieldTest(String startState, String typed, String expectedEndState) {
        this.startState = startState;
        this.typed = typed;
        this.expectedEndState = expectedEndState;
    }

    @Parameterized.Parameters()
    public static List<Object[]> parameters() {
        Object[][] data = {
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

        return Arrays.asList(data);
    }

    @Test
    public void test() throws Exception {
        setupInitialState();

        textField.replaceSelection(typed);

        checkEndState();
    }

    private void setupInitialState() {
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

    private void checkEndState() {
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
