# Smithy Examples

This [Gradle multi-project]((https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:creating_multi_project_builds)) was created by:

1. Initializing a Gradle project (i.e. `gradle init`) for a Java library (`lib/`) that represents the Java implementations of custom traits, transformations and plugins.
2. Creating a directory (`model/`) that represents the Smithy model definition and creating `model/build.gradle` to represent a [multi-project build](https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:creating_multi_project_builds).

Each project (`lib` and `model`) can be built standalone (via `gradle build`) or build together via (`gradlew`/`gradlew.bat`).
