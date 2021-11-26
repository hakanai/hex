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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

/**
 * Convenience methods for access to the format repository.
 *
 * @author trejkaz
 */
public class Repository {
    private static final Path root;
    static {
        FileSystemProvider provider = FileSystemProvider.installedProviders().stream()
            .filter(p -> "jar".equals(p.getScheme())) //NON-NLS
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No jar filesystem provider in JRE"));

        String pathWithinJar = "/org/trypticon/hex/formats/repository/classfile/class_file.rb";
        URL here = Repository.class.getResource(pathWithinJar);
        Path pathToSomeFile;
        if ("jar".equals(here.getProtocol())) {
            try {
                FileSystem jarFileSystem = provider.newFileSystem(here.toURI(), Collections.emptyMap());
                pathToSomeFile = jarFileSystem.getPath(pathWithinJar);
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Malformed URI from hard-coded fragments", e);
            } catch (IOException e) {
                throw new IllegalStateException("Couldn't read from a jar which we're supposedly running from", e);
            }
        } else if ("file".equals(here.getProtocol())) {
            try {
                pathToSomeFile = Paths.get(here.toURI());
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Malformed URI from URL", e);
            }
        } else {
            throw new UnsupportedOperationException("Unsupported URL: " + here);
        }

        root = pathToSomeFile.resolveSibling("..").normalize(); //NON-NLS
    }

    /**
     * Gets the root directory of the in-built script repository.
     * This is supposed to work even if the repository is packaged in a jar.
     *
     * @return the path to the root.
     */
    public static Path getRoot() {
        return root;
    }
}
