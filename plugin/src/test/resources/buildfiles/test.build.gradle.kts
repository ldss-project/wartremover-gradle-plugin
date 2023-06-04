plugins {
    scala
    application
    id("io.github.jahrim.wartremover")
}

repositories { mavenCentral() }

dependencies { implementation("org.scala-lang:scala3-library_3:3.2.2") }

wartremover { configFile(".test.wartremover.conf") }