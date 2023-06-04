package io.github.jahrim.wartremover

import io.github.jahrim.wartremover.tasks.PrintTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.scala.ScalaCompile

/** WartRemover Gradle Plugin. */
class WartRemoverPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("wartremover", WartRemoverExtension::class.java, target)
        target.afterEvaluate {
            target.plugins.withType(ScalaPlugin::class.java) {
                configure(target, extension)
            }
        }
    }

    /**
     * Configure this plugin on the specified project, provided the WartRemover extension.
     * @param target the specified project
     * @param wartRemover the WartRemover extension
     */
    private fun configure(target: Project, wartRemover: WartRemoverExtension): Unit =
        with(target) {
            with(dependencies) {
                add("scalaCompilerPlugins", "org.wartremover:wartremover_${scalaVersion(target)}:+")
            }
            with(tasks) {
                val wartRemoverCompilerOptions: List<String> = wartRemover.compilerOptions()
                withType(ScalaCompile::class.java) {
                    it.scalaCompileOptions.additionalParameters.addAll(wartRemoverCompilerOptions)
                }
                register("wartRemoverCompilerOptions", PrintTask::class.java) {
                    it.group = "WartRemover"
                    it.description = "Shows the WartRemover compiler options given to the scala compiler."
                    it.text.set(
                        wartRemoverCompilerOptions.joinToString("\n").ifBlank {
                            "No configuration found."
                        }
                    )
                }
            }
        }

    /**
     * @param target the specified project
     * @return the scala library version of the specified project.
     */
    private fun scalaVersion(target: Project): String =
        target.configurations
            .filter { listOf("compile", "api", "implementation").contains(it.name) }
            .flatMap { it.dependencies }
            .firstOrNull { it.group == "org.scala-lang" && it.name == "scala3-library_3" }
            ?.version
            ?: error("Scala 3 library not found.")
}
