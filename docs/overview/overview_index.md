---
id: overview_index
title: "Overview"
---

Scala URL Detector is a Scala library that detects and extracts URLs from text. It is based on the fork of LinkedIn Engineering team's open-source library in the following [repository](https://github.com/URL-Detector/URL-Detector).

## Installation

To use the latest snapshot of Scala URL Detector in your project add the following to your build.sbt file:

```scala
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
libraryDependencies += "io.lambdaworks" %% "scurl-detector" % "@SNAPSHOT_VERSION@"
```