# WartRemover Gradle Plugin

[![GitHub Release](https://img.shields.io/github/v/tag/ldss-project/wartremover-gradle-plugin?label=Github&color=blue)](https://github.com/ldss-project/wartremover-gradle-plugin/releases)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.jahrim/wartremover?label=Maven%20Central&color=blue)](https://central.sonatype.com/artifact/io.github.jahrim/wartremover)
[![Gradle Portal Release](https://img.shields.io/gradle-plugin-portal/v/io.github.jahrim.wartremover?label=Gradle%20Plugin%20Portal&color=blue)](https://plugins.gradle.org/plugin/io.github.jahrim.wartremover)
[![Test](https://github.com/ldss-project/wartremover-gradle-plugin/actions/workflows/continuous-testing.yml/badge.svg)](https://github.com/ldss-project/wartremover-gradle-plugin/actions/workflows/continuous-testing.yml)
[![Deployment](https://github.com/ldss-project/wartremover-gradle-plugin/actions/workflows/continuous-deployment.yml/badge.svg)](https://github.com/ldss-project/wartremover-gradle-plugin/actions/workflows/continuous-testing.yml)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fldss-project%2Fwartremover-gradle-plugin.svg)](https://fossa.com/)

Gradle plugin for configuring WartRemover as a code linter in a Scala 3 project.

## Import

In a Scala 3 project, import the plugin inside the Gradle configuration.

```kotlin
plugins {
    scala
    id("io.github.jahrim.wartremover") version "<your-choice>"
}

repositories { 
    mavenCentral() 
}

dependencies { 
    implementation("org.scala-lang:scala3-library_3:<scala-version>") 
}
```

### Dependencies

This plugin only works for Scala 3. Moreover, it depends on 
[WartRemover Linting Tool for Scala](https://github.com/wartremover/wartremover), requiring
a `<scala-version>` for which such tool is available (e.g. `3.2.2`).

## Plugin Configuration

The configuration of WartRemover for your Scala 3 project must be specified as an 
[HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation) 
configuration file inside your project.

The plugin should be configured by specifying where to look for such configuration file. 

```kotlin
wartremover {
    configFile("<path-to-your-configuration-file>")       //default: .wartremover.conf
}
```

### Defaults
If not configured, the default path for that the configuration file is `<your-project>/.wartremover.conf`.
If such file is not found, the user will be notified that a default configuration for WartRemover has been
loaded.

## WartRemover Configuration

Here's the default configuration file applied to WartRemover.

```hocon
warts {                              
  Any = Error                             
  AsInstanceOf = Error               
  DefaultArguments = Error           
  EitherProjectionPartial = Error   
  IsInstanceOf = Error               
  IterableOps = Warning               
  NonUnitStatements = Warning         
  Null = Warning                      
  OptionPartial = Warning             
  Product = Warning                   
  Return = Warning                   
  Serializable = Warning             
  StringPlusAny = Warning            
  Throw = Ignore                   
  TripleQuestionMark = Warning       
  TryPartial = Warning               
  Var = Warning                      
}
```

The configuration file specifies how each wart should be treated by the Scala 3 compiler.

The configuration file must contain the `warts` clause, which is a set of key-value pairs, where the keys are the
wart identifiers and the values are their level of threat, chosen by the user.

There are three levels of threat the user can choose from:
- `Error`: when the wart is found, the compiler will throw an error;
- `Warning`: when the wart is found, the compiler will notify the user with a warning;
- `Ignore`: when the wart is found, the compiler will ignore it.

The documentation for all warts can be found at this [link](https://www.wartremover.org/doc/warts.html). As of now, only
[Unsafe Warts](https://github.com/wartremover/wartremover/blob/master/core/src/main/scala/org/wartremover/warts/Unsafe.scala)
are supported.

### !!! Disclaimer: not all warts have been tested yet !!!

## Disable WartRemover

In your Scala 3 project, you can disable WartRemover for part of your code, by including the following annotation:
`@SuppressWarnings(Array("org.wartremover.warts.<wart-id>"))`.

For example:
```scala
@SuppressWarnings(Array("org.wartremover.warts.Null"))
val myCodeHasToBe = null
```