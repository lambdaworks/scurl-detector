---
id: overview_example
title: "Example"
---

Printing URLs extracted with default options and an allowed URL:

```scala mdoc
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.{Host, AbsoluteUrl}

val detector: UrlDetector           = UrlDetector.default.withAllowed(Set(Host.parse("lambdaworks.io")))
val extractedUrls: Set[AbsoluteUrl] = detector.extract("Hello! This is a URL - lambdaworks.io")

extractedUrls.foreach(println)
```