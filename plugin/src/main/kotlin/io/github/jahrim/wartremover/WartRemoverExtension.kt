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
