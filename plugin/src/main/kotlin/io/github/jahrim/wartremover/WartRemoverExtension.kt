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

import com.github.uharaqo.hocon.mapper.load
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

/**
 * Extension for the WartRemoverPlugin.
 */
open class WartRemoverExtension(target: Project) {
    private val project: Project = target

    /** Compiler configuration for WartRemover. */
    private val configFile: Property<File> =
        target.objects.property(File::class.java).apply { convention(project.file(".wartremover.conf")) }

    /**
     * Set the configuration file for WartRemover.
     *
     * @param path the path to the configuration file for WartRemover from the project directory.
     */
    fun configFile(path: String): Unit = this.configFile.set(project.file(path))

    /**
     * @return the compiler options configured for WartRemover.
     */
    fun compilerOptions(): List<String> =
        WartRemoverCompilerConfiguration.serializer().load(configuration()).toCompilerOptions()

    /**
     * @return the configuration of WartRemover.
     */
    private fun configuration(): Config = when {
        this.configFile.get().exists() -> ConfigFactory.parseFile(this.configFile.get())
        else -> {
            project.logger.warn(
                """
                No configuration file found at '${this.configFile.get().canonicalPath}'
                Loading default configuration...
                """.trimIndent(),
            )
            ConfigFactory.parseResources(javaClass.classLoader, "configs/default.conf")
        }
    }
}
