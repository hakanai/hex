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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import javax.annotation.Nullable;

import com.ibm.icu.text.CollationElementIterator;
import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;
import com.ibm.icu.text.UCharacterIterator;

import org.trypticon.hex.binary.Binary;

/**
 * Searches for text in the binary.
 *
 * @author trejkaz
 */
class TextSearcher implements Searcher {
    private final CollationElementIterator needleIterator;
    private final RuleBasedCollator collator;
    private final CharsetDecoder charsetDecoder;

    private ByteBuffer byteBuffer;
    private CharBuffer charBuffer;

    TextSearcher(String needle, Charset charset) {

        // ICU does this cast itself so it must be safe.
        collator = (RuleBasedCollator) Collator.getInstance();
        collator.setStrength(Collator.IDENTICAL);

        needleIterator = collator.getCollationElementIterator(needle);

        charsetDecoder = charset.newDecoder();
        int likelyMaxSize = (int) Math.ceil(needle.length() * charset.newEncoder().maxBytesPerChar());
        byteBuffer = ByteBuffer.allocate(2 * likelyMaxSize);
        charBuffer = CharBuffer.allocate(2 * needle.length());

    }

    @Nullable
    @Override
    public Match find(Binary haystack, long startPosition, SearchParams params) {
        long position = startPosition;
        long haystackLength = haystack.length();
        boolean wrappedAlready = false;
        while (true) {
            Match matchResult = testForMatch(haystack, position);
            if (matchResult != null) {
                return matchResult;
            }

            if (params.isBackwards()) {
                position --;
                if (position < 0) {
                    if (params.isWrapping() && !wrappedAlready) {
                        wrappedAlready = true;
                        position = haystackLength - 1;
                    } else {
                        break;
                    }
                }
            } else {
                position ++;
                if (position >= haystackLength) {
                    if (params.isWrapping() && !wrappedAlready) {
                        wrappedAlready = true;
                        position = 0;
                    } else {
                        break;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private Match testForMatch(Binary haystack, long position) {
        if (position >= haystack.length()) {
            return null;
        }

        while (true) {
            byteBuffer.clear();
            charBuffer.clear();
            charsetDecoder.reset();
            needleIterator.reset();

            // Read some number of bytes into the byte buffer.
            byteBuffer.limit(Math.min(byteBuffer.capacity(), (int) (haystack.length() - position)));
            haystack.read(position, byteBuffer);
            byteBuffer.flip();

            // Try to convert that to chars.
            boolean endOfInput = byteBuffer.limit() < byteBuffer.capacity();
            CoderResult coderResult = charsetDecoder.decode(byteBuffer, charBuffer, endOfInput);
            if (coderResult.isError()) {
                return null;
            }
            charBuffer.flip();

            CollationElementIterator haystackIterator = collator.getCollationElementIterator(
                UCharacterIterator.getInstance(charBuffer.array(), 0, charBuffer.limit()));

            int firstOrder = haystackIterator.next();
            if (firstOrder == CollationElementIterator.NULLORDER ||
                firstOrder == CollationElementIterator.IGNORABLE) {
                // Special case of having ignorable stuff at the front of the match (e.g. NULs.)
                return null;
            }

            haystackIterator.reset();
            while (true) {
                int matchCharCount = haystackIterator.getOffset();
                int needleOrder = nextNonNullElement(needleIterator);
                int haystackOrder = nextNonNullElement(haystackIterator);

                if (needleOrder == CollationElementIterator.NULLORDER) {
                    // Found a match, but how many bytes was it?
                    int matchByteCount = 0;
                    while (true) {
                        matchByteCount++;

                        byteBuffer.position(0);
                        byteBuffer.limit(matchByteCount);
                        charBuffer.clear();
                        charsetDecoder.decode(byteBuffer, charBuffer, endOfInput);

                        if (charBuffer.position() == matchCharCount) {
                            break;
                        }
                    }

                    // End of the needle, therefore it's a match.
                    return new Match(position, matchByteCount);
                }
                if (haystackOrder == CollationElementIterator.NULLORDER) {
                    if (endOfInput) {
                        return null;
                    }

                    byteBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                    charBuffer = CharBuffer.allocate(charBuffer.capacity() * 2);
                    break; // will go to the outer group
                }

                if (needleOrder != haystackOrder) {
                    // This character doesn't match.
                    return null;
                }
                // Otherwise, back around for the next collation element.
            }
        }
    }

    /**
     * Gets the next non-ignored collation element from the given iterator.
     *
     * @param iterator the iterator.
     * @return the next non-ignored collation element, which cannot possibly be {@code IGNORABLE}.
     */
    private int nextNonNullElement(CollationElementIterator iterator) {
        int order = CollationElementIterator.IGNORABLE;
        while (order != CollationElementIterator.NULLORDER) {
            order = iterator.next();
            if (order != CollationElementIterator.IGNORABLE &&
                order != CollationElementIterator.NULLORDER) {

                return order;
            }
        }

        return CollationElementIterator.NULLORDER;
    }
}
