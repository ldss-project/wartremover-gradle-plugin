package io.github.jahrim.wartremover.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/** Task that shows the specified text on the standard output. */
open class PrintTask : DefaultTask() {
    /** The text that will be shown by this task upon execution. */
    @Input val text: Property<String> = project.objects.property(String::class.java)

    /** Shows the text defined in this task configuration. */
    @TaskAction fun show(): Unit = logger.quiet(text.get())
}
