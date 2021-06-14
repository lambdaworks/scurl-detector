![scala-version][scala-version-badge]  [![Scala CI](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml/badge.svg)](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Scala URL Detector

Scala library that detects and extracts URLs from text. It is created to overcome some of the known issues of LinkedIn Engineering team's open-sourced on the following [repo](https://github.com/linkedin/URL-Detector).


### How to use
To use Scala URL Detector library, import the UrlDetector class and instantiate it with text in which you want to extract URLs from and UrlDetectorOptions you prefer for that text.

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

val detector = UrlDetector("hello this is a url Linkedin.com", UrlDetectorOptions.Default)
val extractedUrls: List[Url] = detector.extract()
extractedUrls.foreach(println)
```

[scala-version-badge]: https://img.shields.io/badge/scala-2.13.6-blue?logo=scala&color=teal
