# Smithy Examples - Model

This project contains the Smithy model definition and the build logic.  
The [Smithy Gradle plugin](https://awslabs.github.io/smithy/1.0/guides/building-models/gradle-plugin.html#smithy-gradle-plugin) is used to build the model.

It depends on [`lib/`](../lib) for custom traits, transformations plugins.

## Getting Started

Run `gradle build` to build the Smithy model, which outputs to `build/smithyprojections/model/`.  

`build/smithyprojections/model/source` contains the original model.
