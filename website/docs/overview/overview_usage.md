---
id: overview_usage
title: "Usage"
---

## UrlDetector

To use the Scala URL Detector library, you need to import the `UrlDetector` class:

```scala
import io.lambdaworks.detection.UrlDetector
```

When creating an instance of this class using `new`, you have to provide a `Config` which affects the behavior of the detector:

```scala
final class UrlDetector(config: Config)
```

An `apply` method is defined inside the companion object, so you can instantiate this class without using the `new` keyword. There is another `apply` method with no parameters that uses the default configuration.

## Config

`Config` is a case class in which you can specify `options` in the form of `UrlDetectorOptions`, as well a set of `allowed` and `denied` URLs of type `Set[Url]`:

```scala
final case class Config(
  options: UrlDetectorOptions,
  allowed: Set[Url],
  denied: Set[Url]
)
```

`allowed` represents URLs which the detector is supposed to detect, while `denied` specifies URLs which the detector should ignore.
You can get the default `Config` using `Config.default`:

```scala
object Config {

  lazy val default: Config = Config(UrlDetectorOptions.Default, Set.empty, Set.empty)

}
```

You can create a new `Config` from an existing one using the following `Config` methods:

```scala
def withOptions(options: UrlDetectorOptions): Config

def withAllowed(urls: Set[Url]): Config

def withDenied(urls: Set[Url]): Config 
```

## UrlDetectorOptions

`UrlDetectorOptions` is a Sum type, with all the case objects defined in the [UrlDetectorOptions.scala](https://github.com/lambdaworks/scurl-detector/blob/main/src/main/scala/io/lambdaworks/detection/UrlDetectorOptions.scala) file.

## Extracting

In order to extract URLs from a `String` using an instance of `UrlDetector`, you need to call the `extract` method with that `String`, which will return `Set[Url]`:

```scala
def extract(content: String): Set[Url]
```

`Url` is a value class which has the following methods:

```scala
def host: String

def containedIn(urls: Set[Url]): Boolean
```

You can get the `String` representation of the URL with the `toString` method, and there is also an `apply` method defined in the companion object for constructing a `Url` from a `String`:

```scala
def apply(url: String): Url
```