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

plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.trypticon.hex:hex-dependencies:0.8"))

    constraints {
        api("com.fifesoft:rsyntaxtextarea:2.5.0")
        api("com.jgoodies:jgoodies-forms:1.7.2")
        api("com.jgoodies:jgoodies-common:1.7.0")
        api("com.jtechdev:macwidgets:1.0.1")
        api("de.sciss:syntaxpane:1.1.2")
        api("org.hamcrest:hamcrest:2.2")
        api("org.jetbrains:annotations:23.0.0")
        api("org.jruby:jruby-complete:9.2.17.0")
        api("org.trypticon.gum:gum:0.1")
        api("org.trypticon.haqua:haqua:0.1")
        api("org.yaml:snakeyaml:1.13")
    }
}
