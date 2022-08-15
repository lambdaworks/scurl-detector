---
id: overview_example
title: "Example"
---

Printing URLs extracted with default options and an allowlist:

```scala
import io.lambdaworks.detection.{Url, UrlDetector, Config}

val detector: UrlDetector    = UrlDetector(Config.default.withAllowlist(List("https://lambdaworks.io/")))
val extractedUrls: List[Url] = detector.extract("Hello! This is a URL - lambdaworks.io")

extractedUrls.foreach(println)
```