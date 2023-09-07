/*
MIT License

Copyright (c) 2023 Cesario Jahrim Gabriele

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
     *
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
                        },
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
