plugins {
    scala
    application
    id("io.github.jahrim.wartremover") version "0.1.0-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies { implementation(libs.scala) }

application { mainClass.set("io.github.jahrim.wartremover.WartRemoverTest") }

wartremover { configFile(".wartremover-custom.conf") }
