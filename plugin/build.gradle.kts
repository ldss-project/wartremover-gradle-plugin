private class ProjectInfo {
    companion object {
        const val longName: String = "WartRemover Gradle Plugin"
        const val description: String = "A plugin for applying WartRemover code linting to scala, by means of a configuration file."
        const val website: String = "https://github.com/ldss-project/wartremover-gradle-plugin"

        const val pluginGroup: String = "io.github.jahrim"
        val pluginId: String = "${pluginGroup}.wartremover"
        const val implementationClass: String = "io.github.jahrim.wartremover.WartRemoverPlugin"
        val tags = listOf("wartremover", "code linter", "scala")
    }
}

plugins {
    with(libs.plugins) {
        alias(kotlin)
        alias(kotlin.doc)
        alias(kotlin.qa)
        alias(kotlin.serialization)
        alias(task.tree.generator)
        alias(git.semantic.versioning)
        alias(publish)
        signing
    }
}

repositories { mavenCentral() }

dependencies {
    implementation(libs.bundles.kotlin.serialization.bundle)
    implementation(gradleApi())
    testImplementation(libs.bundles.kotest.bundle)
    testImplementation(gradleTestKit())
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }

// Publication
val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(tasks.compileKotlin.get().sources)
    from(tasks.processResources.get().source)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc.get().outputDirectory)
}

group = ProjectInfo.pluginGroup
gitSemVer {
    buildMetadataSeparator.set("-")
    assignGitSemanticVersion()
}

gradlePlugin {
    this.website.set(ProjectInfo.website)
    this.vcsUrl.set("${ProjectInfo.website}.git")
    plugins {
        create("wartremover") {
            id = ProjectInfo.pluginId
            displayName = ProjectInfo.longName
            description = ProjectInfo.description
            tags.set(ProjectInfo.tags)
            implementationClass = ProjectInfo.implementationClass
        }
    }
}

publishing {
    publications.create<MavenPublication>("pluginMaven") {
        pom {
            name.set(ProjectInfo.longName)
            description.set(ProjectInfo.description)
            url.set(ProjectInfo.website)

            licenses {
                license {
                    name.set("The MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

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

            repositories {
                maven {
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        val mavenUsername: String? by project
                        val mavenPassword: String? by project
                        username = mavenUsername
                        password = mavenPassword
                    }
                }
            }
        }
        signing { sign(publishing.publications["pluginMaven"]) }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}
