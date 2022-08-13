---
id: overview_usage
title: "Usage"
---

## UrlDetector

To use the Scala URL Detector library, you need to import the `UrlDetector` class:

```scala
import io.lambdaworks.detection.UrlDetector
```

When creating an instance of this class, you can optionally provide a `Config` which affects the behavior of the detector:

```scala
class UrlDetector(config: Config = Config())
```

An `apply` method is defined inside the companion object, so you can instantiate this class without using the `new` keyword.

## Config

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
You can get the default `Config` using `Config.default` as well.

You can create a new `Config` from an existing one using the following `Config` methods:

```scala
def withOptions(options: UrlDetectorOptions): Config

def withAllowlist(urls: List[String]): Config

def withDenylist(urls: List[String]): Config 
```

## UrlDetectorOptions

`UrlDetectorOptions` extends `StringEnumEntry` from the [Enumeratum](https://github.com/lloydmeta/enumeratum) library. You can find all of the enumeration entries in the [UrlDetectorOptions.scala](https://github.com/lambdaworks/scurl-detector/blob/main/src/main/scala/io/lambdaworks/detection/UrlDetectorOptions.scala) file.

## Extracting

In order to extract URLs from a `String` using an instance of `UrlDetector`, you need to call the `extract` method with that `String`, which will return `List[Url]`:

```scala
def extract(content: String): List[Url]
```

`Url` is a value class which has the following methods:

```scala
def host: String

def containedIn(urls: List[Url]): Boolean
```

You can get the `String` representation of the URL with the `toString` method, and there is also an `apply` method defined in the companion object for constructing a `Url` from a `String`:

```scala
def apply(url: String): Url
```