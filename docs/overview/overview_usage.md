---
id: overview_usage
title: "Usage Guide"
---

## Core Concepts

### URL Representation

This library uses the [scala-uri](https://github.com/lemonlabsuk/scala-uri) library for representing URLs. All extracted URLs are returned as `AbsoluteUrl` instances, which provide a type-safe, immutable representation of fully-qualified URLs.

```scala
import io.lemonlabs.uri.AbsoluteUrl

// AbsoluteUrl has convenient accessors:
val url: AbsoluteUrl = // ... extracted URL
url.schemeOption  // Some("https")
url.host          // example.com
url.port          // Some(8080) or None
url.path          // /api/v1/users
url.query         // QueryString with parameters
url.fragment      // Some("section") or None
```

## UrlDetector

The `UrlDetector` class is the main entry point for URL extraction.

### Creating a Detector

#### Using Default Configuration

```scala
import io.lambdaworks.detection.UrlDetector

val detector = UrlDetector.default
```

#### Using Specific Options

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

val htmlDetector = UrlDetector(UrlDetectorOptions.Html)
val jsonDetector = UrlDetector(UrlDetectorOptions.Json)
val xmlDetector = UrlDetector(UrlDetectorOptions.Xml)
```

### Builder Pattern

`UrlDetector` follows an immutable builder pattern. All methods return new instances:

```scala
// Each method returns a new detector
val detector = UrlDetector.default
  .withAllowed(Host.parse("example.com"))
  .withDenied(Host.parse("ads.example.com"))
```

### Configuring Detection Options

Change detection options on an existing detector:

```scala
val defaultDetector = UrlDetector.default
val htmlDetector = defaultDetector.withOptions(UrlDetectorOptions.Html)
```

## Host Filtering

### Allowing Specific Hosts

Extract URLs only from allowed hosts:

```scala
import io.lemonlabs.uri.Host

val detector = UrlDetector.default.withAllowed(
  Host.parse("example.com"),
  Host.parse("github.com")
)

val urls = detector.extract("Visit example.com, github.com, and other.com")
// Returns only: example.com, github.com
```

**Subdomain Matching:**
- `example.com` matches `www.example.com`, `api.example.com`, etc.
- `www` subdomain is automatically assumed
- All subdomains are included unless specifically denied

### Denying Specific Hosts

Exclude URLs from specific hosts:

```scala
val detector = UrlDetector.default.withDenied(
  Host.parse("ads.example.com"),
  Host.parse("tracking.example.com")
)

val urls = detector.extract("Visit example.com and ads.example.com")
// Returns only: example.com
```

**Important:** Denied hosts take precedence over allowed hosts.

### Combining Allow and Deny

```scala
val detector = UrlDetector.default
  .withAllowed(Host.parse("example.com"))      // Allow example.com and all subdomains
  .withDenied(Host.parse("ads.example.com"))   // Except ads.example.com

val urls = detector.extract("""
  https://example.com
  https://api.example.com
  https://ads.example.com
""")
// Returns: example.com, api.example.com
// Excludes: ads.example.com
```

## UrlDetectorOptions

`UrlDetectorOptions` is a sealed trait (sum type) that defines different detection modes optimized for specific content formats.

### Available Options

| Option | Use Case | Documentation |
|--------|----------|---------------|
| `Default` | Plain text | Basic URL detection |
| `QuoteMatch` | Double-quoted URLs | Strips matching `"` |
| `SingleQuoteMatch` | Single-quoted URLs | Strips matching `'` |
| `BracketMatch` | Bracketed URLs | Handles `()`, `{}`, `[]` |
| `Json` | JSON content | JSON-optimized detection |
| `Javascript` | JavaScript code | JS-optimized detection |
| `Xml` | XML documents | XML-optimized detection |
| `Html` | HTML pages | HTML-optimized detection |
| `AllowSingleLevelDomain` | Localhost/internal | Allows single-level domains |

For detailed information about each option, see the [Detection Options Reference](overview_options.md).

### Choosing the Right Option

```scala
// For HTML content
val htmlDetector = UrlDetector(UrlDetectorOptions.Html)
htmlDetector.extract("<a href='https://example.com'>Link</a>")

// For JSON API responses
val jsonDetector = UrlDetector(UrlDetectorOptions.Json)
jsonDetector.extract("""{"url": "https://api.example.com"}""")

// For development (allows localhost)
val devDetector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
devDetector.extract("http://localhost:8080")
```

## Extracting URLs

### Basic Extraction

```scala
val detector = UrlDetector.default
val text = "Visit https://example.com and www.github.com"
val urls: Set[AbsoluteUrl] = detector.extract(text)

urls.foreach(println)
```

### Extraction Behavior

**Scheme Handling:**
- URLs without schemes get `http://` by default
- Protocol-relative URLs (`//example.com`) become `http://example.com`
- Explicit schemes are preserved

```scala
val urls = detector.extract("""
  example.com              → http://example.com
  https://example.com      → https://example.com
  //cdn.example.com        → http://cdn.example.com
""")
```

**Filtering:**
- Email addresses are automatically excluded
- Invalid URLs are silently filtered out
- Duplicate URLs are removed (returns a `Set`)

**Result Type:**
- Returns `Set[AbsoluteUrl]` (immutable, no duplicates)
- Empty set if no URLs found (never returns `null`)
- No exceptions thrown for invalid URLs

### Working with Results

```scala
val urls = detector.extract(text)

// Check if URLs were found
if (urls.nonEmpty) {
  println(s"Found ${urls.size} URLs")
}

// Iterate over URLs
urls.foreach { url =>
  println(s"${url.host} - ${url}")
}

// Filter by scheme
val httpsOnly = urls.filter(_.schemeOption.contains("https"))

// Group by host
val byHost = urls.groupBy(_.host)

// Convert to list
val urlList = urls.toList.sortBy(_.toString)
```

## Complete Example

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}
import io.lemonlabs.uri.{Host, AbsoluteUrl}

object UrlExtractor {
  def extractFromHtml(htmlContent: String): Set[AbsoluteUrl] = {
    UrlDetector(UrlDetectorOptions.Html)
      .withAllowed(
        Host.parse("example.com"),
        Host.parse("cdn.example.com")
      )
      .withDenied(Host.parse("ads.example.com"))
      .extract(htmlContent)
  }

  def extractHttpsOnly(text: String): Set[AbsoluteUrl] = {
    UrlDetector.default
      .extract(text)
      .filter(_.schemeOption.contains("https"))
  }
}

// Usage
val html = """
  <a href="https://example.com">Home</a>
  <img src="https://cdn.example.com/logo.png">
  <script src="https://ads.example.com/track.js"></script>
"""

val urls = UrlExtractor.extractFromHtml(html)
urls.foreach(println)
```

## Thread Safety

`UrlDetector` instances are immutable and thread-safe. You can safely share them across threads:

```scala
object Detectors {
  // Safe to use across multiple threads
  val default: UrlDetector = UrlDetector.default
  val html: UrlDetector = UrlDetector(UrlDetectorOptions.Html)
}

// Concurrent usage is safe
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val futures = documents.map { doc =>
  Future {
    Detectors.html.extract(doc)
  }
}
```

## Next Steps

- See [Examples](overview_example.md) for more comprehensive examples
- Review [Detection Options](overview_options.md) for detailed option descriptions
- Explore [Advanced Usage](overview_advanced.md) for sophisticated patterns
- Check the [API Reference](overview_api.md) for complete method documentation
- Visit [FAQ](overview_faq.md) if you encounter issues