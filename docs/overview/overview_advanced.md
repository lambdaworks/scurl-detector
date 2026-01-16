---
id: overview_advanced
title: "Advanced Usage"
---

## Host Filtering with Subdomains

The URL detector provides intelligent subdomain handling when filtering hosts.

### Subdomain Matching Rules

When you specify a host, the detector automatically handles subdomains intelligently:

```scala
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.Host

// Specifying "example.com" will match:
// - example.com
// - www.example.com (www is implicit)
// - api.example.com
// - any.subdomain.example.com

val detector = UrlDetector.default.withAllowed(Host.parse("example.com"))
val urls = detector.extract("""
  https://example.com
  https://www.example.com
  https://api.example.com
  https://cdn.us-east.example.com
""")

// All URLs will be extracted
```

### Denying Specific Subdomains

You can deny specific subdomains while allowing others:

```scala
val detector = UrlDetector.default
  .withAllowed(Host.parse("example.com"))
  .withDenied(Host.parse("ads.example.com"))

val text = """
  https://example.com
  https://api.example.com
  https://ads.example.com
  https://tracking.ads.example.com
"""

val urls = detector.extract(text)
// Returns: example.com, api.example.com
// Excludes: ads.example.com, tracking.ads.example.com
```

### Explicit Subdomain Filtering

To match only a specific subdomain (not its children):

```scala
// This will match api.example.com and www.api.example.com
// but NOT v1.api.example.com
val detector = UrlDetector.default.withAllowed(Host.parse("api.example.com"))
```

## Protocol-Relative URLs

The detector handles protocol-relative URLs (starting with `//`) by converting them to HTTP:

```scala
val detector = UrlDetector.default
val urls = detector.extract("Load //cdn.example.com/script.js")

// Returns: http://cdn.example.com/script.js
```

## Missing Scheme Handling

URLs without schemes are automatically assigned the HTTP scheme:

```scala
val detector = UrlDetector.default
val urls = detector.extract("Visit example.com and www.github.com")

// Returns:
// - http://example.com
// - http://www.github.com
```

## IPv4 and IPv6 Support

The detector recognizes both IPv4 and IPv6 addresses:

```scala
val detector = UrlDetector.default

// IPv4
val ipv4Urls = detector.extract("API at http://192.168.1.1:8080/api")
// Returns: http://192.168.1.1:8080/api

// IPv6
val ipv6Urls = detector.extract("Server at http://[2001:db8::1]:8080/")
// Returns: http://[2001:db8::1]:8080/
```

## URL Encoding and Special Characters

The detector handles URL-encoded characters, particularly encoded spaces:

```scala
val detector = UrlDetector.default
val urls = detector.extract("Download from https://example.com/my%20file.pdf")

// The URL is properly extracted with encoding preserved
```

## Email Address Filtering

The detector automatically filters out email addresses to avoid false positives:

```scala
val detector = UrlDetector.default
val text = "Contact us at support@example.com or visit https://example.com"

val urls = detector.extract(text)
// Returns only: https://example.com
// Excludes: support@example.com
```

## Handling URLs with User Info

URLs containing user credentials require explicit schemes:

```scala
val detector = UrlDetector.default

// This will be extracted (has explicit scheme)
val withScheme = detector.extract("ftp://user:pass@ftp.example.com")
// Returns: ftp://user:pass@ftp.example.com

// This will be rejected (no scheme with user info)
val noScheme = detector.extract("user:pass@example.com")
// Returns: empty set (rejected as potentially an email variant)
```

## Custom Detection Pipelines

You can create reusable detector configurations for different use cases:

```scala
object Detectors {
  // For public web scraping - only allow common public domains
  lazy val publicWeb: UrlDetector = UrlDetector(UrlDetectorOptions.Html)
    .withDenied(
      Host.parse("localhost"),
      Host.parse("127.0.0.1"),
      Host.parse("0.0.0.0")
    )

  // For API response parsing
  lazy val apiResponses: UrlDetector = UrlDetector(UrlDetectorOptions.Json)

  // For development environments
  lazy val development: UrlDetector =
    UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)

  // For secure contexts - HTTPS only (filter applied post-extraction)
  def httpsOnly: UrlDetector = UrlDetector.default

  def extractHttpsOnly(text: String): Set[AbsoluteUrl] = {
    httpsOnly.extract(text).filter(_.schemeOption.contains("https"))
  }
}

// Usage
val webUrls = Detectors.publicWeb.extract(htmlContent)
val apiUrls = Detectors.apiResponses.extract(jsonResponse)
val localUrls = Detectors.development.extract(configFile)
val secureUrls = Detectors.extractHttpsOnly(userInput)
```

## Processing Large Text

For large documents, consider chunking the text and processing in parallel:

```scala
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

def extractUrlsParallel(text: String, chunkSize: Int = 10000)
                       (implicit ec: ExecutionContext): Future[Set[AbsoluteUrl]] = {
  val chunks = text.grouped(chunkSize).toSeq
  val detector = UrlDetector.default

  val futures = chunks.map { chunk =>
    Future {
      detector.extract(chunk)
    }
  }

  Future.sequence(futures).map(_.flatten.toSet)
}

// Usage
val largeText = // ... load large document
val urlsFuture = extractUrlsParallel(largeText)
```

## Validating Extracted URLs

You can add additional validation after extraction:

```scala
import io.lemonlabs.uri.AbsoluteUrl

def validateUrls(urls: Set[AbsoluteUrl]): Set[AbsoluteUrl] = {
  urls.filter { url =>
    // Only allow standard ports
    url.port.forall(p => p == 80 || p == 443 || p == 8080)
  }.filter { url =>
    // Only allow certain schemes
    url.schemeOption.exists(s => Set("http", "https").contains(s))
  }.filter { url =>
    // Exclude URLs with certain path patterns
    !url.path.toString().contains("/admin/")
  }
}

val detector = UrlDetector.default
val allUrls = detector.extract(text)
val validUrls = validateUrls(allUrls)
```

## Extracting and Categorizing URLs

Group and categorize extracted URLs:

```scala
case class UrlCategory(
  apis: Set[AbsoluteUrl],
  assets: Set[AbsoluteUrl],
  pages: Set[AbsoluteUrl],
  other: Set[AbsoluteUrl]
)

def categorizeUrls(urls: Set[AbsoluteUrl]): UrlCategory = {
  val (apis, rest1) = urls.partition(_.path.toString().contains("/api/"))
  val (assets, rest2) = rest1.partition { url =>
    val path = url.path.toString().toLowerCase
    path.endsWith(".css") || path.endsWith(".js") ||
    path.endsWith(".png") || path.endsWith(".jpg")
  }
  val (pages, other) = rest2.partition { url =>
    val path = url.path.toString().toLowerCase
    path.isEmpty || path.endsWith(".html") || path.endsWith("/")
  }

  UrlCategory(apis, assets, pages, other)
}

// Usage
val detector = UrlDetector(UrlDetectorOptions.Html)
val urls = detector.extract(htmlContent)
val categorized = categorizeUrls(urls)

println(s"APIs: ${categorized.apis.size}")
println(s"Assets: ${categorized.assets.size}")
println(s"Pages: ${categorized.pages.size}")
println(s"Other: ${categorized.other.size}")
```

## Integration with HTTP Clients

Use extracted URLs with HTTP clients:

```scala
import sttp.client3._

def validateExtractedUrls(text: String): Future[Map[AbsoluteUrl, Boolean]] = {
  val detector = UrlDetector.default
  val urls = detector.extract(text)
  val backend = HttpURLConnectionBackend()

  val results = urls.map { url =>
    val request = basicRequest.get(uri"${url.toString}").response(asString)
    val response = request.send(backend)
    url -> response.code.isSuccess
  }.toMap

  Future.successful(results)
}
```

## Thread Safety

`UrlDetector` instances are immutable and thread-safe. You can safely share detector instances across threads:

```scala
object SharedDetectors {
  // Safe to use across multiple threads
  val default: UrlDetector = UrlDetector.default
  val html: UrlDetector = UrlDetector(UrlDetectorOptions.Html)
  val json: UrlDetector = UrlDetector(UrlDetectorOptions.Json)
}

// Safe concurrent usage
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def processMultipleTexts(texts: Seq[String]): Future[Seq[Set[AbsoluteUrl]]] = {
  Future.traverse(texts) { text =>
    Future {
      SharedDetectors.default.extract(text)
    }
  }
}
```

## Performance Optimization Tips

1. **Reuse Detector Instances**: Create detector instances once and reuse them
2. **Choose Specific Options**: Use the most specific detection option for your content type
3. **Apply Host Filters**: If you know you only need specific hosts, apply filters to reduce processing
4. **Pre-filter Text**: If possible, exclude large sections of text that definitely don't contain URLs
5. **Batch Processing**: Process multiple documents with the same detector instance

```scala
// Good: Reuse detector instance
val detector = UrlDetector(UrlDetectorOptions.Json)
val results = documents.map(doc => detector.extract(doc))

// Less efficient: Create new detector each time
val results = documents.map { doc =>
  UrlDetector(UrlDetectorOptions.Json).extract(doc)
}
```
