---
id: overview_example
title: "Example"
---

Printing URLs extracted with default options and an allowed URL:

```scala
import io.lambdaworks.detection.{Url, UrlDetector, Config}

val detector: UrlDetector    = UrlDetector(Config.default.withAllowed(Set(Url("https://lambdaworks.io/"))))
val extractedUrls: Set[Url] = detector.extract("Hello! This is a URL - lambdaworks.io")

extractedUrls.foreach(println)
```