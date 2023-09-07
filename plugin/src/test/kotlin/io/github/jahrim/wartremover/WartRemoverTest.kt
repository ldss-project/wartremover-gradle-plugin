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
