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
    `java-library`
}

dependencies {
    implementation(platform(project(":hex-app-dependencies")))
    implementation(project(":hex-formats"))
    implementation("com.fifesoft:rsyntaxtextarea")
    implementation("com.google.code.findbugs:jsr305")
    implementation("com.jgoodies:jgoodies-common")
    implementation("com.jgoodies:jgoodies-forms")
    implementation("com.jtechdev:macwidgets")
    implementation("org.jetbrains:annotations")
    implementation("org.trypticon.hex:hex-anno")
    implementation("org.trypticon.hex:hex-binary")
    implementation("org.trypticon.hex:hex-interpreter")
    implementation("org.trypticon.hex:hex-viewer")
    implementation("org.trypticon.hex:hex-util")
    implementation("org.swinglabs.swingx:swingx-action")
    implementation("org.swinglabs.swingx:swingx-common")
    implementation("org.swinglabs.swingx:swingx-core")
    implementation("org.swinglabs.swingx:swingx-painters")
    implementation("org.swinglabs.swingx:swingx-plaf")
}
