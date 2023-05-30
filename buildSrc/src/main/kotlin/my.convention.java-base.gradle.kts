import de.thetaphi.forbiddenapis.gradle.CheckForbiddenApisExtension
import de.thetaphi.forbiddenapis.gradle.ForbiddenApisPlugin
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.ErrorPronePlugin
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.NullAwayExtension
import net.ltgt.gradle.nullaway.NullAwayPlugin
import net.ltgt.gradle.nullaway.nullaway
import org.gradle.kotlin.dsl.`java-base`
import org.gradle.kotlin.dsl.`java-library`

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
    `java-base`
    id("my.convention.base")

    id("de.thetaphi.forbiddenapis")
    id("net.ltgt.errorprone")
    id("net.ltgt.nullaway")

    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile>().configureEach {
    // How is this still not the default, Gradle?!
    options.encoding = "UTF-8"
    options.compilerArgs = listOf("-Xlint:all")

    options.errorprone.nullaway {
        severity.set(if (name.contains("Test")) CheckSeverity.OFF else CheckSeverity.ERROR)
    }
}

nullaway {
    annotatedPackages.add("org.trypticon.hex")
}

forbiddenApis {
    bundledSignatures = setOf("jdk-unsafe", "jdk-deprecated", "jdk-system-out")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            url = uri(if (version.toString().contains("SNAPSHOT")) {
                "https://oss.sonatype.org/content/repositories/snapshots"
            } else {
                "https://oss.sonatype.org/service/local/staging/deploy/maven2"
            })
            credentials {
                username = System.getenv("DEPLOY_USER")
                password = System.getenv("DEPLOY_PASS")
            }
        }
    }

    publications {
        register<MavenPublication>("mavenJava") {
            pom {
                name.set("Hex Components")
                description.set("A collection of UI components for rendering binary files as hexadecimal")
                inceptionYear.set("2009")
                url.set("https://github.com/hakanai/hex-components")
                licenses {
                    license {
                        name.set("GNU Lesser General Public License, Version 3")
                        url.set("https://www.gnu.org/licenses/lgpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set("hakanai")
                        name.set("Hakanai")
                        email.set("hakanai@ephemeral.garden")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/hakanai/hex-components.git")
                    developerConnection.set("scm:git:ssh://github.com/hakanai/hex-components.git")
                    url.set("http://github.com/hakanai/hex-components/")
                }
            }
        }
    }
}

signing {
    sign(extensions.getByType<PublishingExtension>().publications["mavenJava"])
}
