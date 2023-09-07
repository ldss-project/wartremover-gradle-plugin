---
title: Homepage
layout: default
nav_order: 1
---

# WartRemover Gradle Plugin
{: .no_toc}

---

## Development

### Purpose

The [WartRemover Gradle Plugin](https://github.com/ldss-project/wartremover-gradle-plugin) is a
plugin for Gradle whose goal is to simplify the configuration of the code linter
[WartRemover](https://www.wartremover.org/) in Scala 3 projects.

The [WartRemover Gradle Plugin](https://github.com/ldss-project/wartremover-gradle-plugin) relies
on the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover) for the Scala 3
compiler, which is used to search the user's code for _warts_ during compilation.

This plugin offers two basic services:
- It automatically applies the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover) to
  the Scala 3 compiler used by Gradle.

  > **Note**: this service requires the user to apply the [Scala Gradle Plugin](https://docs.gradle.org/current/userguide/scala_plugin.html)
  to his Gradle project, so that this plugin can configure the Scala 3 compiler. Moreover, it requires the user
  to provide a Scala 3 dependency supported by the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover)
  (e.g. `org.scala-lang:scala3-library_3:3.2.2`).
- It can convert proper [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
  configuration files into compiler options for the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover).

### Implementation

The implementation of this plugin is described by the following UML Class Diagram.

![UML Class Diagram](/wartremover-gradle-plugin/resources/images/class-diagram.png)

#### WartRemoverPlugin
The `WartRemoverPlugin` is a `org.gradle.api.Plugin`, so that it can be applied to
a Gradle project.

When applied, the `WartRemoverPlugin` does the following:
1. Provides an extension for configuring the plugin, called `wartremover`;
2. Adds the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover) to the
   project dependencies, applying it to the Scala 3 compiler, after the project evaluation.

   The version of the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover) is
   computed from the version of the Scala 3 dependency provided by the user;
3. Configures the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover)
   using the [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
   configuration file provided by the user, after the project evaluation;
4. Provides a task to print to the console the compiler options provided by the user, called
   `wartRemoverCompilerOptions`.

#### WartRemoverExtension

The `wartremover` extension is modelled by the class `WartRemoverExtension`, which provides
methods to set the [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
configuration file used to compute the compiler options for the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover)
and to retrieve those compiler options.

These methods can be accessed also by the user as follows:

```kotlin
// WartRemover extension
wartremover {
    // Set the HOCON configuration file used to compute the WartRemover compiler options
    configFile(".custom-wartremover.conf")
    // Retrieve the computed WartRemover compiler options
    val options = compilerOptions()
}
```

When calling the `configFile` method, the user can set the path to the
[HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
configuration file to be parsed by this plugin. By default, this is set
to a `.wartremover.conf` file within the root directory of the user's project.

When calling the `compilerOptions` method, the `WartRemoverExtension` parses the [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
configuration file provided by the user, producing the corresponding `WartRemoverCompilerConfiguration`, which is a type
of `CompilerConfiguration`. If no configuration file is provided, the `WartRemoverExtension` produces the default
`WartRemoverCompilerConfiguration`.

A `CompilerConfiguration` is a list of compiler options that can be applied to a certain compiler. In particular,
a `WartRemoverCompilerConfiguration` is a set of compiler options to be applied to the Scala 3 compiler for
configuring the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover).

A `WartRemoverCompilerConfiguration` can be defined as a map from _wart identifiers_ to `TraverserType`s, where
a `TraverserType` defines the threat level assigned to the _wart_ when it is found during compilation.

When calling its method `toCompilerOptions`, the `WartRemoverCompilerConfiguration` produces a list of compiler
options for configuring the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover), computed
from its internal map.

These compiler options are finally applied by the `WartRemoverPlugin` to the Scala 3 compiler, after the evaluation
of the project.

#### PrintTask

The task `wartRemoverCompilerOptions` is a type of `PrintTask`, where a `PrintTask` is a task which takes
a text as input to be printed to the console when called by the user.

In particular, the task `wartRemoverCompilerOptions` is configured within the `WartRemoverPlugin` to print
the compiler options computed for configuring the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover).

### Testing

The plugin has been tested using the [Gradle TestKit](https://docs.gradle.org/current/userguide/test_kit.html)
on a _build file_ defined specifically for testing purposes.

---

[Back to Top](#top)