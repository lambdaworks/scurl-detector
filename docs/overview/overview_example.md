---
id: overview_example
title: "Example"
---

Printing URLs extracted with default options and an allowed URL:

```scala
import io.lambdaworks.detection.{UrlDetector, Config}
import io.lemonlabs.uri.{Host, AbsoluteUrl}

val detector: UrlDetector           = UrlDetector(Config.default.withAllowed(Set(Host.parse("lambdaworks.io"))))
val extractedUrls: Set[AbsoluteUrl] = detector.extract("Hello! This is a URL - lambdaworks.io")

extractedUrls.foreach(println)
```