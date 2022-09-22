---
id: overview_usage
title: "Usage"
---

## URLs

This library uses the [scala-uri](https://github.com/lemonlabsuk/scala-uri) library for representing URLs.

## UrlDetector

To use the Scala URL Detector library, you need to import the `UrlDetector` class:

```scala
import io.lambdaworks.detection.UrlDetector
```

An `apply` method is defined inside the companion object for instantiating a `UrlDetector`:

```scala
object UrlDetector {

  def apply(options: UrlDetectorOptions): UrlDetector
  
}
```

Where `options` specify the configuration of the `UrlDetector`.

If you want to instantiate a `UrlDetector` with the default configuration, you can use `UrlDetector.default`:

```scala
object UrlDetector {

  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default)

}
````

You can create a new `UrlDetector` from an existing one using the following `UrlDetector` methods:

```scala
def withOptions(options: UrlDetectorOptions): UrlDetector

def withAllowed(host: Host, hosts: Host*): UrlDetector

def withDenied(host: Host, hosts: Host*): UrlDetector 
```

Where with `withAllowed` we specify hosts of URLs which the detector is supposed to detect, while with `withDenied` we specify hosts of URLs which the detector should ignore. You don't have to specify a www subdomain for hosts, as it is assumed. Unless another subdomain is specified, all possible subdomains will be matched.

## UrlDetectorOptions

`UrlDetectorOptions` is a Sum type, with all the case objects defined in the [UrlDetectorOptions.scala](https://github.com/lambdaworks/scurl-detector/blob/main/src/main/scala/io/lambdaworks/detection/UrlDetectorOptions.scala) file.

## Extracting

In order to extract URLs from a `String` using an instance of `UrlDetector`, you need to call the `extract` method with that `String`, which will return `Set[AbsoluteUrl]`:

```scala
def extract(content: String): Set[AbsoluteUrl]
```

If a URL inside the specified `content` doesn't have a scheme specified, it will be returned with a http scheme.