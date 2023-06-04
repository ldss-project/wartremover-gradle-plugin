package io.github.jahrim.wartremover

import com.github.uharaqo.hocon.mapper.load
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File
import kotlin.io.path.createTempDirectory

/**
 * Basic Test for WartRemover.
 */
class WartRemoverTest : StringSpec({
    "the task 'wartRemoverCompilerOptions' should show the WartRemover compiler configuration of the user" {
        with(javaClass.classLoader) {
            // Build project directory
            val buildFile = getResource("buildfiles/test.build.gradle.kts")
            val configFile = getResource("configs/.test.wartremover.conf")

            val projectDirectory: File = createTempDirectory().toFile().also { it.mkdirs() }
            File(projectDirectory, "build.gradle.kts").writeText(buildFile?.readText().orEmpty())
            File(projectDirectory, ".test.wartremover.conf").writeText(configFile?.readText().orEmpty())

            // Load expected compiler options
            val configuration: Config = ConfigFactory.parseURL(configFile)
            val expectedCompilerOptions: List<String> =
                WartRemoverCompilerConfiguration.serializer().load(configuration).toCompilerOptions()

            // Build project, producing actual compiler options
            val result = GradleRunner.create()
                .withProjectDir(projectDirectory)
                .withPluginClasspath()
                .withArguments("wartRemoverCompilerOptions")
                .build()
            println(result.output)

            // Compare expected and actual compiler options
            result.tasks.forEach { it.outcome shouldNotBe TaskOutcome.FAILED }
            expectedCompilerOptions.forEach { result.output shouldContain it }
        }
    }
})
