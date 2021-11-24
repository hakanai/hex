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

package org.trypticon.hex.formats;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import org.trypticon.hex.formats.ruby.RubyStructureDSL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests for {@link Repository}.
 */
public class RepositoryTest {
    @Test
    public void testReadingRoot() throws Exception {
        String[] names;
        try (Stream<Path> children = Files.list(Repository.getRoot())) {
            names = children
                .map(p -> p.getFileName().toString())
                .toArray(String[]::new);
        }

        assertThat(names, hasItemInArray("classfile")); //NON-NLS
        assertThat(names, hasItemInArray("gif")); //NON-NLS
        assertThat(names, hasItemInArray("jpeg")); //NON-NLS
    }

    @Test
    public void testLoadingScript() throws Exception {
        Structure structure = RubyStructureDSL.loadFromFile(Repository.getRoot().resolve("classfile/class_file.rb"));
        assertThat(structure, is(notNullValue()));
        //TODO? structure.drop()
    }
}
