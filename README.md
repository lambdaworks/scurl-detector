![scala-version][scala-version-badge]  [![Scala CI](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml/badge.svg)](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Scala URL Detector

Scala library that detects and extracts URLs from text. It is created based on forked library of LinkedIn Engineering team's open-sourced on the following [repo](https://github.com/URL-Detector/URL-Detector).


### How to use
To use Scala URL Detector library, import the UrlDetector class and instantiate it with text in which you want to extract URLs from and configuration you prefer for that text. Config can contain UrlDetectorOptions, allowlist and denylist. Allowlist is list of URLs that are allowed to be detected and denylist is list of URLs that are not allowed to be detected.

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions, Config}

val detector = UrlDetector("hello this is a url lambdaworks.io", Config(UrlDetectorOptions.Default, List("https://lambdaworks.io/"), Nil))
val extractedUrls: List[Url] = detector.extract()
extractedUrls.foreach(println)
```

If you prefer default options you can omit Config when instantiating UrlDetector.

```scala
import io.lambdaworks.detection.{UrlDetector}

val detector = UrlDetector("hello this is a url lambdaworks.io")
val extractedUrls: List[Url] = detector.extract()
extractedUrls.foreach(println)
```


[scala-version-badge]: https://img.shields.io/badge/scala-2.13.6-blue?logo=scala&color=teal
