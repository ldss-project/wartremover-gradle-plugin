plugins {
    scala
    application
    id("io.github.jahrim.wartremover") version "0.1.0-dev0a+d2f9970"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies { implementation(libs.scala) }

application { mainClass.set("io.github.jahrim.wartremover.WartRemoverTest") }

wartremover { configFile(".wartremover-custom.conf") }
