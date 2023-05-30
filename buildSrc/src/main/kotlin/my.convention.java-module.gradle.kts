import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra

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

plugins {
    // Counter-intuitively, application plugin does depend on java-library.
    `java-library`
    id("my.convention.java-base")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    errorprone("com.google.errorprone:error_prone_core:2.19.1")
    errorprone("com.uber.nullaway:nullaway:0.10.10")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    options {
        // Workaround for addBooleanOption not working
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}

tasks.jar {
    manifest {
        attributes("Copyright" to project.extra["copyright"])
    }
}
