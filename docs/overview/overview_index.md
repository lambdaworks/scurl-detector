---
id: overview_index
title: "Overview"
---

Scala URL Detector is a Scala library that detects and extracts URLs from text. It is based on the fork of LinkedIn Engineering team's open-source library in the following [repository](https://github.com/URL-Detector/URL-Detector).

## Installation

To use the latest release of Scala URL Detector in your project add the following to your `build.sbt` file:

```scala mdoc:passthrough
println(s"""```scala""")
println(s"""libraryDependencies += "${detection.BuildInfo.organization}" %% "${detection.BuildInfo.name}" % "${detection.BuildInfo.version.split('+').head}"""")
println(s"""```""")
```