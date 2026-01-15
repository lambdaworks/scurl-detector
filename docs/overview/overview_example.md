---
id: overview_example
title: "Examples"
---

## Basic Usage

Extracting URLs with default options:

```scala mdoc
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.{Host, AbsoluteUrl}

val detector: UrlDetector = UrlDetector.default
val text = "Visit https://example.com and www.lambdaworks.io for more info"
val extractedUrls: Set[AbsoluteUrl] = detector.extract(text)

extractedUrls.foreach(println)
```

## Host Filtering

### Allowing Specific Hosts

Extract URLs only from specific domains:

```scala mdoc
val allowedDetector = UrlDetector.default.withAllowed(Host.parse("lambdaworks.io"))
val mixedText = "Check out lambdaworks.io and example.com"
val allowedUrls = allowedDetector.extract(mixedText)

// Only returns lambdaworks.io
allowedUrls.foreach(println)
```

### Denying Specific Hosts

Exclude URLs from specific domains:

```scala mdoc
val deniedDetector = UrlDetector.default.withDenied(Host.parse("ads.example.com"))
val adsText = "Visit example.com but not ads.example.com"
val filteredUrls = deniedDetector.extract(adsText)

// Returns example.com but not ads.example.com
filteredUrls.foreach(println)
```

### Multiple Allowed Hosts

Allow multiple domains:

```scala mdoc
val multiAllowed = UrlDetector.default
  .withAllowed(
    Host.parse("lambdaworks.io"),
    Host.parse("github.com"),
    Host.parse("scala-lang.org")
  )

val techText = "Visit lambdaworks.io, github.com, example.com, and scala-lang.org"
val techUrls = multiAllowed.extract(techText)

// Returns only allowed domains
techUrls.foreach(println)
```

## Format-Specific Detection

### JSON Content

Extracting URLs from JSON:

```scala mdoc
import io.lambdaworks.detection.UrlDetectorOptions

val jsonDetector = UrlDetector(UrlDetectorOptions.Json)
val jsonContent = """
{
  "api": "https://api.example.com/v1",
  "docs": "https://docs.example.com",
  "links": ["https://github.com/example", "https://twitter.com/example"]
}
"""
val jsonUrls = jsonDetector.extract(jsonContent)

jsonUrls.foreach(println)
```

### HTML Content

Extracting URLs from HTML:

```scala mdoc
val htmlDetector = UrlDetector(UrlDetectorOptions.Html)
val htmlContent = """
<html>
  <a href="https://example.com">Link</a>
  <img src="https://cdn.example.com/image.png">
  <script>fetch("https://api.example.com/data")</script>
</html>
"""
val htmlUrls = htmlDetector.extract(htmlContent)

htmlUrls.foreach(println)
```

### JavaScript Code

Extracting URLs from JavaScript:

```scala mdoc
val jsDetector = UrlDetector(UrlDetectorOptions.Javascript)
val jsCode = """
const API_URL = 'https://api.example.com';
const CDN = "https://cdn.example.com";
fetch('https://data.example.com/users')
  .then(res => res.json())
"""
val jsUrls = jsDetector.extract(jsCode)

jsUrls.foreach(println)
```

### XML Content

Extracting URLs from XML:

```scala mdoc
val xmlDetector = UrlDetector(UrlDetectorOptions.Xml)
val xmlContent = """<?xml version="1.0"?>
<config>
  <endpoint>https://api.example.com</endpoint>
  <resource href="https://cdn.example.com/data"/>
</config>
"""
val xmlUrls = xmlDetector.extract(xmlContent)

xmlUrls.foreach(println)
```

## Advanced Scenarios

### URLs in Brackets

Detecting URLs in Markdown-style links:

```scala mdoc
val bracketDetector = UrlDetector(UrlDetectorOptions.BracketMatch)
val markdownText = """
See [https://docs.example.com] for documentation.
Also check (https://github.com/example) and {https://support.example.com}
"""
val bracketUrls = bracketDetector.extract(markdownText)

bracketUrls.foreach(println)
```

### Single-Level Domains

Allowing localhost and internal domains:

```scala mdoc
val localDetector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
val devText = """
Development: http://localhost:8080
Internal: http://intranet
Go link: go/documentation
"""
val localUrls = localDetector.extract(devText)

localUrls.foreach(println)
```

### Combining Options with Filtering

Using specific detection mode with host filtering:

```scala mdoc
val combinedDetector = UrlDetector(UrlDetectorOptions.Html)
  .withAllowed(Host.parse("cdn.example.com"), Host.parse("api.example.com"))
  .withDenied(Host.parse("ads.cdn.example.com"))

val complexHtml = """
<html>
  <link href="https://cdn.example.com/styles.css">
  <script src="https://ads.cdn.example.com/tracker.js"></script>
  <img src="https://api.example.com/images/logo.png">
  <a href="https://other.example.com">External</a>
</html>
"""
val combinedUrls = combinedDetector.extract(complexHtml)

// Returns cdn.example.com and api.example.com, but excludes ads.cdn.example.com and other.example.com
combinedUrls.foreach(println)
```

## Working with Extracted URLs

### Accessing URL Components

The extracted URLs are `AbsoluteUrl` instances from scala-uri:

```scala mdoc
val urlComponentsDetector = UrlDetector.default
val componentUrls = urlComponentsDetector.extract("https://api.example.com:8080/v1/users?page=1#top")

componentUrls.foreach { url =>
  println(s"Scheme: ${url.schemeOption}")
  println(s"Host: ${url.host}")
  println(s"Port: ${url.port}")
  println(s"Path: ${url.path}")
  println(s"Query: ${url.query}")
  println(s"Fragment: ${url.fragment}")
}
```

### Filtering by Scheme

Filter extracted URLs by scheme:

```scala mdoc
val schemeUrls = UrlDetector.default.extract("Visit https://secure.example.com and http://legacy.example.com")
val httpsOnly = schemeUrls.filter(_.schemeOption.contains("https"))

httpsOnly.foreach(println)
```

### Grouping by Host

Group URLs by their host:

```scala mdoc
val multiSiteText = """
Check https://github.com/user1, https://github.com/user2,
https://gitlab.com/project, and https://bitbucket.org/repo
"""
val groupedUrls = UrlDetector.default.extract(multiSiteText)
val byHost = groupedUrls.groupBy(_.host)

byHost.foreach { case (host, urls) =>
  println(s"$host: ${urls.size} URLs")
}
```