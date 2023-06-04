private class ProjectInfo {
    companion object {
        const val longName: String = "WartRemover Gradle Plugin"
        const val description: String = "A plugin for applying WartRemover code linting to scala, by means of a configuration file."
        const val website: String = "https://github.com/ldss-project/wartremover-gradle-plugin"

        const val pluginId: String = "io.github.jahrim.wartremover"
        const val version: String = "0.1.0-SNAPSHOT"
        const val implementationClass: String = "io.github.jahrim.wartremover.WartRemoverPlugin"
    }
}
group = ProjectInfo.pluginId
version = ProjectInfo.version

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.doc)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.kotlin.serialization)
    `java-gradle-plugin`
    `maven-publish`
    signing
}

repositories { mavenCentral() }

dependencies {
    implementation(libs.bundles.kotlin.serialization.bundle)
    implementation(gradleApi())
    testImplementation(libs.bundles.kotest.bundle)
    testImplementation(gradleTestKit())
}

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

// Publication
gradlePlugin {
    plugins {
        create("wartremover") {
            id = ProjectInfo.pluginId
            implementationClass = ProjectInfo.implementationClass
        }
    }
}

val sourceJarTask by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(tasks.compileKotlin.get().sources)
    from(tasks.processResources.get().source)
}

val docJarTask by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc.get().outputDirectory)
}

publishing {
    publications.create<MavenPublication>("pluginMaven") {
        setArtifacts(listOf(sourceJarTask, docJarTask))

        pom {
            name.set(ProjectInfo.longName)
            description.set(ProjectInfo.description)
            url.set(ProjectInfo.website)

            /* TODO
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            */

            developers {
                developer {
                    name.set("Jahrim Gabriele Cesario")
                    email.set("jahrim.cesario2@studio.unibo.it")
                    url.set("https://jahrim.github.io/")
                }
            }

            scm {
                connection.set("${ProjectInfo.website}.git")
                developerConnection.set("scm:git:${ProjectInfo.website}.git")
                url.set(ProjectInfo.website)
            }
        }
    }
    signing { sign(publishing.publications["pluginMaven"]) }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}
