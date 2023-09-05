private class ProjectInfo {
    companion object {
        const val longName: String = "WartRemover Gradle Plugin"
        const val description: String = "A plugin for applying WartRemover code linting to scala, by means of a configuration file."

        const val repositoryOwner: String = "ldss-project"
        const val repositoryName: String = "wartremover-gradle-plugin"

        const val artifactGroup: String = "io.github.jahrim"
        const val artifactId: String = "wartremover"
        const val implementationClass: String = "io.github.jahrim.wartremover.WartRemoverPlugin"

        const val license = "The MIT License"
        const val licenseUrl = "https://opensource.org/licenses/MIT"

        val website = "https://github.com/$repositoryOwner/$repositoryName"
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

group = ProjectInfo.artifactGroup
gitSemVer {
    buildMetadataSeparator.set("-")
    assignGitSemanticVersion()
}

gradlePlugin {
    website.set(ProjectInfo.website)
    vcsUrl.set("${ProjectInfo.website}.git")
    plugins {
        create("wartremover") {
            id = "${ProjectInfo.artifactGroup}.${ProjectInfo.artifactId}"
            displayName = ProjectInfo.longName
            description = ProjectInfo.description
            tags.set(ProjectInfo.tags)
            implementationClass = ProjectInfo.implementationClass
        }
    }
}

val setupPublishPlugin by tasks.registering {
    val gradlePublishKey: String? by project
    val gradlePublishSecret: String? by project
    gradlePublishKey?.apply{ System.setProperty("gradle.publish.key", this) }
    gradlePublishSecret?.apply{ System.setProperty("gradle.publish.secret", this) }
}
tasks.named("publishPlugins"){ dependsOn(setupPublishPlugin) }

publishing {
    publications.withType(MavenPublication::class.java) {
        pom {
            name.set(ProjectInfo.longName)
            description.set(ProjectInfo.description)
            url.set(ProjectInfo.website)

            licenses {
                license {
                    name.set(ProjectInfo.license)
                    url.set(ProjectInfo.licenseUrl)
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
                url.set(ProjectInfo.website)
                connection.set("$url.git")
                developerConnection.set("scm:git:$url.git")
            }
        }
    }
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                val mavenCentralUsername: String? by project
                val mavenCentralPassword: String? by project
                username = mavenCentralUsername
                password = mavenCentralPassword
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}
