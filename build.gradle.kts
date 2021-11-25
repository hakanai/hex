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

import de.thetaphi.forbiddenapis.gradle.CheckForbiddenApisExtension
import de.thetaphi.forbiddenapis.gradle.ForbiddenApisPlugin
import net.ltgt.gradle.errorprone.ErrorPronePlugin
import net.ltgt.gradle.nullaway.NullAwayExtension
import net.ltgt.gradle.nullaway.NullAwayPlugin
import net.ltgt.gradle.errorprone.*
import net.ltgt.gradle.nullaway.nullaway

plugins {
    id("de.thetaphi.forbiddenapis") version "3.1"
    id("net.ltgt.errorprone") version "1.3.0"
    id("net.ltgt.nullaway") version "1.0.2"
}

allprojects {
    repositories {
        jcenter()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.swinglabs.swingx:swingx-action"))
                    .with(module("com.github.hakanai.swingx:swingx-action:master-SNAPSHOT"))
            substitute(module("org.swinglabs.swingx:swingx-common"))
                    .with(module("com.github.hakanai.swingx:swingx-common:master-SNAPSHOT"))
            substitute(module("org.swinglabs.swingx:swingx-core"))
                    .with(module("com.github.hakanai.swingx:swingx-core:master-SNAPSHOT"))
            substitute(module("org.swinglabs.swingx:swingx-painters"))
                    .with(module("com.github.hakanai.swingx:swingx-painters:master-SNAPSHOT"))
            substitute(module("org.swinglabs.swingx:swingx-plaf"))
                    .with(module("com.github.hakanai.swingx:swingx-plaf:master-SNAPSHOT"))
        }
    }

    version = "0.4"
    group = "org.trypticon.hex"
    extra["copyright"] = "Copyright \u00A9 2009-2014,2016-2017,2021  Hex Project"

    plugins.withType<JavaPlugin> {
        plugins.apply(ForbiddenApisPlugin::class)
        plugins.apply(ErrorPronePlugin::class)
        plugins.apply(NullAwayPlugin::class)

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_11
            withJavadocJar()
            withSourcesJar()
        }

        tasks.withType<JavaCompile>().configureEach {
            // How is this still not the default, Gradle?!
            options.encoding = "UTF-8"
            options.compilerArgs = listOf("-Xlint:all")

            options.errorprone.nullaway {
                severity.set(if (name.contains("Test")) CheckSeverity.OFF else CheckSeverity.ERROR)
            }
        }

        configure<NullAwayExtension> {
            annotatedPackages.add("org.trypticon.hex")
        }

        configure<CheckForbiddenApisExtension> {
            bundledSignatures = setOf("jdk-unsafe", "jdk-deprecated", "jdk-system-out")
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
        dependencies {
            "testImplementation"("org.junit.jupiter:junit-jupiter-api")
            "testImplementation"("org.junit.jupiter:junit-jupiter-params")
            "testImplementation"("org.hamcrest:hamcrest")
            "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine")
            "errorprone"("com.google.errorprone:error_prone_core:2.4.0")
            "errorprone"("com.uber.nullaway:nullaway:0.8.0")
            "errorproneJavac"("com.google.errorprone:javac:9+181-r4173-1")
        }

        tasks.named<Jar>("jar") {
            manifest {
                attributes("Copyright" to project.extra["copyright"])
            }
        }
    }
}
