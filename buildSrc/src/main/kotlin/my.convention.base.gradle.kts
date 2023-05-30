/*
 * Hex - a hex viewer and annotator
 * Copyright (C) 2009-2014,2016-2017,2021  Hakanai, Hex Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

configurations.all {
    resolutionStrategy.dependencySubstitution {
        // Using stand-in dependencies for swingx, because the real thing never gets a release these days.
        substitute(module("org.swinglabs.swingx:swingx-action"))
                .using(module("com.github.hakanai.swingx:swingx-action:master-SNAPSHOT"))
        substitute(module("org.swinglabs.swingx:swingx-common"))
                .using(module("com.github.hakanai.swingx:swingx-common:master-SNAPSHOT"))
        substitute(module("org.swinglabs.swingx:swingx-core"))
                .using(module("com.github.hakanai.swingx:swingx-core:master-SNAPSHOT"))
        substitute(module("org.swinglabs.swingx:swingx-painters"))
                .using(module("com.github.hakanai.swingx:swingx-painters:master-SNAPSHOT"))
        substitute(module("org.swinglabs.swingx:swingx-plaf"))
                .using(module("com.github.hakanai.swingx:swingx-plaf:master-SNAPSHOT"))
    }
}

version = "0.4"
group = "org.trypticon.hex"
extra["copyright"] = "Copyright \u00A9 2009-2014,2016-2017,2021,2023  Hex Project"
