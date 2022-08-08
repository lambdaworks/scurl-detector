![scala-version][scala-version-badge]
[![Scala CI](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml/badge.svg)](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Scala URL Detector

Scala library that detects and extracts URLs from text. It is based on the fork of LinkedIn Engineering team's open-source library in the following [repository](https://github.com/URL-Detector/URL-Detector).

## Usage

### `UrlDetector` 

To use the Scala URL Detector library, you need to import the `UrlDetector` case class:

```scala
import io.lambdaworks.detection.UrlDetector
```

In order to create an instance of this class, you need to provide a `String` from which you'd like to extract URLs, and optionally provide a `Config` which affects the behavior of the detector:

```scala
final case class UrlDetector(content: String, config: Config = Config())
```

### `Config`

`Config` is a case class in which you can specify the options in the form of `UrlDetectorOptions`, as well as an allowlist and a denylist of type `List[String]`:

```scala
final case class Config(
  options: UrlDetectorOptions = UrlDetectorOptions.Default,
  allowlist: List[String] = Nil,
  denylist: List[String] = Nil
)
```

Allowlist represents URLs which the detector is supposed to detect, while denylist specifies URLs which the detector should ignore.
If a value for a parameter is not specified, the above default value for it is used.

You can create a `UrlDetector` from an existing one with a different `Config` using the following `UrlDetector` methods:

```scala
def withOptions(options: UrlDetectorOptions): UrlDetector

def withAllowlist(urls: List[String]): UrlDetector

def withDenylist(urls: List[String]): UrlDetector 
```

### `UrlDetectorOptions`

`UrlDetectorOptions` extends `StringEnumEntry` from the [Enumeratum](https://github.com/lloydmeta/enumeratum) library. You can find all of the enumeration entries in the [UrlDetectorOptions.scala](https://github.com/lambdaworks/scurl-detector/blob/main/src/main/scala/io/lambdaworks/detection/UrlDetectorOptions.scala) file.

### Extracting

In order to extract URLs from an instance of a `UrlDetector`, you need to call the `extract()` method, which returns `List[Url]`:

```scala
def extract: List[Url]
```

`Url` is a value class which has the following methods:

```scala
def getHost: String

def contained(urls: List[Url]): Boolean
```

You can get the `String` representation of the URL with the `toString()` method, and there is also an apply method defined in the companion object for constructing a `Url` from a `String`:

```scala
def apply(url: String): Url
```

### Example

Printing URLs extracted with default options and an allowlist:

```scala
import io.lambdaworks.detection.{Url, UrlDetector, UrlDetectorOptions, Config}

val detector: UrlDetector    = UrlDetector("Hello! This is a URL - lambdaworks.io", Config(UrlDetectorOptions.Default, List("https://lambdaworks.io/"), Nil))
val extractedUrls: List[Url] = detector.extract

extractedUrls.foreach(println)
```

Providing the allowlist after creating a `UrlDetector`:

```scala
import io.lambdaworks.detection.{Url, UrlDetector, UrlDetectorOptions, Config}

val detector: UrlDetector    = UrlDetector("Hello! This is a URL - lambdaworks.io")
val extractedUrls: List[Url] = detector.withAllowlist(List("https://lambdaworks.io/")).extract

extractedUrls.foreach(println)
```

[scala-version-badge]: https://img.shields.io/badge/scala-2.13.8-blue?logo=scala&color=teal