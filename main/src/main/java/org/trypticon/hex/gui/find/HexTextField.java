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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import org.trypticon.hex.binary.Binary;
import org.trypticon.hex.binary.BinaryFactory;

/**
 * A text field for entering hex to search for.
 *
 * @author trejkaz
 */
public class HexTextField extends JTextField {

    /**
     * Constructs the text field.
     *
     * @param text the initial text.
     * @param columns the number of columns to display by default.
     */
    public HexTextField(String text, int columns) {
        super(columns);

        PlainDocument document = new PlainDocument();
        document.setDocumentFilter(new HexDocumentFilter());
        setDocument(document);

        setText(text);
    }

    /**
     * Gets the value of the text field as binary.
     *
     * @return the binary. The binary will be in-memory and thus doesn't need to be closed.
     */
    public Binary getBinary() {
        String text = getText();
        byte[] bytes = new byte[text.length() / 3];
        for (int i = 0, j = 0; i < bytes.length; i++, j += 3) {
            bytes[i] = Byte.parseByte(text.substring(j, j + 2), 16);
        }
        return BinaryFactory.wrap(bytes);
    }

    /**
     * Custom filter to keep the document length at a multiple of 3.
     */
    private class HexDocumentFilter extends DocumentFilter {
        private final Pattern nonHex = Pattern.compile("[^0-9a-fA-F]+");

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (length == 0) {
                return;
            }

            int endOffset = offset + length;
            String insertExtra = null;

            if (length == 1) {
                // Special case for single character deletes because if you tried to delete a single
                // space, nothing would happen.
                switch (offset % 3) {
                    // In all cases, select the whole byte.
                    case 0:
                        length = 3;
                        break;
                    case 1:
                        offset--;
                        length = 3;
                        break;
                    case 2:
                        offset -= 2;
                        length = 3;
                        break;
                }
            } else {
                if (offset % 3 == 2) {
                    offset++;
                    length--;
                }
                if (endOffset % 3 == 2) {
                    endOffset++;
                    length++;
                }

                if (offset % 3 == 1) {
                    // Both in the middle is a special case where the values are already correct.
                    if (endOffset % 3 == 0) {
                        // Only the first byte cut in half so insert the last nibble for it (and the space.)
                        insertExtra = "0 ";
                    }
                } else { // offset % 3 == 0
                    if (endOffset % 3 == 1) {
                        // Only the last byte cut in half so insert the first nibble for it.
                        insertExtra = "0";
                    }
                }
            }

            super.remove(fb, offset, length);
            setCaretPosition(offset);
            if (insertExtra != null) {
                super.insertString(fb, offset, insertExtra, null); // no attrs, I guess this is fine.
                moveCaretPosition(offset + 1);
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

            if (string == null || string.isEmpty()) {
                return;
            }

            string = strip(string);

            StringBuilder builder = new StringBuilder();
            boolean leftover = string.length() % 2 != 0;
            switch (offset % 3) {
                case 2:
                    // Caret is at the end of a hex digit. Treat this as if inserting at the next offset.
                    offset++;
                    // Deliberate fall-through to case 0.

                case 0: {
                    // Caret is at the start of a hex digit.
                    for (int i = 0; i < string.length(); i += 2) {
                        if (i + 2 > string.length()) {
                            builder.append(string.charAt(i));
                            builder.append("0 ");
                        } else {
                            builder.append(string.substring(i, i + 2));
                            builder.append(' ');
                        }
                    }

                    super.insertString(fb, offset, builder.toString(), attr);
                    if (leftover) {
                        setCaretPosition(offset + builder.length() - 2);
                        moveCaretPosition(offset + builder.length() - 1);
                    } else {
                        setCaretPosition(offset + builder.length());
                    }
                    break;
                }

                case 1: {
                    // Caret is between the two nibbles so the first char is finishing the byte.
                    builder.append(string.charAt(0));
                    builder.append(' ');
                    for (int i = 1; i < string.length(); i += 2) {
                        if (i + 2 > string.length()) {
                            builder.append(string.charAt(i));
                        } else {
                            builder.append(string.substring(i, i + 2));
                            builder.append(' ');
                        }
                    }
                    if (leftover) {
                        builder.append('0');
                    }

                    super.insertString(fb, offset, builder.toString(), attr);
                    if (leftover) {
                        setCaretPosition(offset + builder.length() - 1);
                        moveCaretPosition(offset + builder.length());
                    } else {
                        setCaretPosition(offset + builder.length());
                    }
                    break;
                }

            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            text = strip(text);
            if (length == 1 && text.length() == 1) {
                int mod = offset % 3;
                if (mod != 2) {
                    // Trivial case of replacing one digit with one digit.
                    super.replace(fb, offset, length, text, attrs);
                    setCaretPosition(offset + (mod == 1 ? 2 : 1));
                    return;
                }
            }

            remove(fb, offset, length);
            insertString(fb, offset, text, attrs);
        }

        /**
         * Strips any characters from the string which are not hex digits and also converts to uppercase.
         *
         * @param text the text.
         * @return the stripped text.
         */
        private String strip(String text) {
            Matcher matcher = nonHex.matcher(text);
            StringBuffer replaced = new StringBuffer(text.length());
            while (matcher.find()) {
                matcher.appendReplacement(replaced, "");
            }
            matcher.appendTail(replaced);
            return replaced.toString().toUpperCase();
        }
    }
}
