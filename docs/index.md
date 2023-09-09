---
title: Homepage
layout: default
nav_order: 1
---

# WartRemover Gradle Plugin
{: .no_toc}

## Index
{: .no_toc}

- TOC
{:toc}

---

## Purpose

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

## Implementation

The implementation of this plugin is described by the following UML Class Diagram,
which will be explained in the following sections.

![UML Class Diagram](/wartremover-gradle-plugin/resources/images/class-diagram.png)

### WartRemoverPlugin
The `WartRemoverPlugin` is a `org.gradle.api.Plugin`, meaning that it can be applied to
a Gradle project as any other Gradle plugin.

When applied, the `WartRemoverPlugin` does the following:
1. Provide an extension called `wartremover`, whose purpose is to configure this plugin;
2. After the project evaluation:
   1. Add the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover) to the
      project dependencies, applying it to the Scala 3 compiler.

      The version of the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover) is
      computed from the version of the Scala 3 dependency provided by the user;
   2. Configure the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover)
      using the [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
      configuration file provided by the user;
   3. Provide a task called `wartRemoverCompilerOptions`, whose purpose is to print to the console the
      compiler options provided by the user.

### WartRemoverExtension

The `wartremover` extension is modelled by the class `WartRemoverExtension`, which provides the
following methods:
- `configFile`: set the path to the HOCON configuration file used to compute the compiler options
  for the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover). By default,
  this is set to the `.wartremover.conf` file within the root directory of the user's project.
- `compilerOptions`: parse the [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
  configuration file provided by the user, producing the compiler options for the
  [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover). If no configuration
  file is provided, it produces the default compiler options.

These methods can be accessed also by the user as follows:

```kotlin
// build.gradle.kts
wartremover {
    configFile(".custom-wartremover.conf")
    val options = compilerOptions()
}
```

The compiler options for the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover)
are modelled by the class `WartRemoverCompilerConfiguration`, which is a type of `CompilerConfiguration`.

A `CompilerConfiguration` is a list of compiler options that can be applied to a certain compiler. In particular,
a `WartRemoverCompilerConfiguration` is a set of compiler options to be applied to the Scala 3 compiler for
configuring the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover).

A `WartRemoverCompilerConfiguration` can be defined as a map from _wart identifiers_ to `TraverserType`s, where
a `TraverserType` defines the threat level assigned to a _wart_ when it is found during compilation.

When calling its method `toCompilerOptions`, the `WartRemoverCompilerConfiguration` produces a list of compiler
options for configuring the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover), computed
from its internal map.

These compiler options are finally applied by the `WartRemoverPlugin` to the Scala 3 compiler, after the evaluation
of the project.

### Tasks

- **wartRemoverCompilerOptions**

    The task `wartRemoverCompilerOptions` is a type of `PrintTask`, where a `PrintTask` is a task which takes
    a text as input to be printed to the console when called by the user.
    
    In particular, the task `wartRemoverCompilerOptions` is defined by the `WartRemoverPlugin` after the project
    evaluation, in order to print the compiler options computed for configuring the
    [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover).

## Testing

The plugin has been tested using the [Gradle TestKit](https://docs.gradle.org/current/userguide/test_kit.html)
on a _build file_ defined specifically for testing purposes.

The goal of the provided tests was mainly to verify that this plugin could be applied to a Gradle
project and that the compiler options for the [WartRemover Compiler Plugin](https://github.com/wartremover/wartremover)
were computed correctly from the [HOCON](https://github.com/lightbend/config/blob/main/HOCON.md#hocon-human-optimized-config-object-notation)
configuration file provided by the user.

The plugin has also been manually tested against several Scala 3 projects.

> **Note**: the test suite should be extended to verify that all _warts_ are identified correctly within
> some sample Scala 3 projects.

---

[Back to Top](#top)