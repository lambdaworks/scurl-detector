---
id: overview_api
title: "API Reference"
---

## UrlDetector

The main class for detecting and extracting URLs from text.

### Constructors

#### `UrlDetector.apply`

Creates a new UrlDetector with the specified options.

```scala
def apply(options: UrlDetectorOptions): UrlDetector
```

**Parameters:**
- `options`: The detection mode to use (see [UrlDetectorOptions](#urldetectoroptions))

**Returns:** A new `UrlDetector` instance

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.Html)
```

---

#### `UrlDetector.default`

Returns a default UrlDetector instance with `UrlDetectorOptions.Default`.

```scala
lazy val default: UrlDetector
```

**Returns:** A default `UrlDetector` instance

**Example:**
```scala
val detector = UrlDetector.default
```

---

### Methods

#### `extract`

Extracts all URLs from the given text content.

```scala
def extract(content: String): Set[AbsoluteUrl]
```

**Parameters:**
- `content`: The text to extract URLs from

**Returns:** A `Set` of `AbsoluteUrl` containing all detected and validated URLs

**Behavior:**
- URLs without schemes are assigned `http://`
- Protocol-relative URLs (`//example.com`) are converted to `http://example.com`
- Invalid URLs are filtered out (returns empty set, doesn't throw)
- Email addresses are excluded
- Duplicate URLs are removed (Set deduplication)

**Example:**
```scala
val detector = UrlDetector.default
val urls = detector.extract("Visit https://example.com and www.github.com")
// Returns: Set(https://example.com, http://www.github.com)
```

---

#### `withAllowed`

Creates a new detector that only extracts URLs from the specified hosts.

```scala
def withAllowed(host: Host, hosts: Host*): UrlDetector
```

**Parameters:**
- `host`: The first allowed host (required)
- `hosts`: Additional allowed hosts (variadic)

**Returns:** A new `UrlDetector` instance with allowed host filtering

**Behavior:**
- Only URLs from specified hosts (and their subdomains) are extracted
- `www` subdomain is implicitly matched
- Subdomains of allowed hosts are included
- If a URL doesn't match any allowed host, it's excluded

**Example:**
```scala
import io.lemonlabs.uri.Host

val detector = UrlDetector.default
  .withAllowed(Host.parse("example.com"), Host.parse("github.com"))

val urls = detector.extract("Visit example.com, github.com, and other.com")
// Returns only: example.com, github.com
```

---

#### `withDenied`

Creates a new detector that excludes URLs from the specified hosts.

```scala
def withDenied(host: Host, hosts: Host*): UrlDetector
```

**Parameters:**
- `host`: The first denied host (required)
- `hosts`: Additional denied hosts (variadic)

**Returns:** A new `UrlDetector` instance with denied host filtering

**Behavior:**
- URLs from specified hosts (and their subdomains) are excluded
- Denied hosts take precedence over allowed hosts
- Subdomains of denied hosts are also excluded

**Example:**
```scala
import io.lemonlabs.uri.Host

val detector = UrlDetector.default
  .withDenied(Host.parse("ads.example.com"))

val urls = detector.extract("Visit example.com and ads.example.com")
// Returns only: example.com
```

---

#### `withOptions`

Creates a new detector with different detection options.

```scala
def withOptions(options: UrlDetectorOptions): UrlDetector
```

**Parameters:**
- `options`: The new detection mode to use

**Returns:** A new `UrlDetector` instance with updated options

**Example:**
```scala
val htmlDetector = UrlDetector.default.withOptions(UrlDetectorOptions.Html)
```

---

## UrlDetectorOptions

A sealed trait representing different detection modes.

### Options

#### `Default`

Basic URL detection without special delimiter handling.

```scala
case object Default extends UrlDetectorOptions
```

**Use case:** Plain text with clearly separated URLs

---

#### `QuoteMatch`

Strips matching double quotes from URL boundaries.

```scala
case object QuoteMatch extends UrlDetectorOptions
```

**Use case:** Text with double-quoted URLs

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.QuoteMatch)
// "https://example.com" → https://example.com
```

---

#### `SingleQuoteMatch`

Strips matching single quotes from URL boundaries.

```scala
case object SingleQuoteMatch extends UrlDetectorOptions
```

**Use case:** Text with single-quoted URLs (shell scripts, etc.)

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.SingleQuoteMatch)
// 'https://example.com' → https://example.com
```

---

#### `BracketMatch`

Handles bracket characters: `()`, `{}`, `[]`.

```scala
case object BracketMatch extends UrlDetectorOptions
```

**Use case:** Markdown, documentation with bracketed URLs

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.BracketMatch)
// (https://example.com) → https://example.com
```

---

#### `Json`

Optimized for JSON content.

```scala
case object Json extends UrlDetectorOptions
```

**Use case:** JSON documents, API responses

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.Json)
// {"url": "https://example.com"} → https://example.com
```

---

#### `Javascript`

Optimized for JavaScript code. Combines JSON rules with single quote handling.

```scala
case object Javascript extends UrlDetectorOptions
```

**Use case:** JavaScript source code, scripts

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.Javascript)
// const url = 'https://example.com'; → https://example.com
```

---

#### `Xml`

Optimized for XML documents.

```scala
case object Xml extends UrlDetectorOptions
```

**Use case:** XML files, SVG, XML-based configs

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.Xml)
// <url>https://example.com</url> → https://example.com
```

---

#### `Html`

Optimized for HTML content.

```scala
case object Html extends UrlDetectorOptions
```

**Use case:** HTML pages, web scraping

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.Html)
// <a href="https://example.com">Link</a> → https://example.com
```

---

#### `AllowSingleLevelDomain`

Allows single-level domains without public suffixes.

```scala
case object AllowSingleLevelDomain extends UrlDetectorOptions
```

**Use case:** Localhost, internal networks, custom TLDs

**Example:**
```scala
val detector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
// http://localhost:8080 → http://localhost:8080
// go/docs → http://go/docs
```

---

## AbsoluteUrl

Represents a fully qualified URL with scheme and host. Part of the [scala-uri](https://github.com/lemonlabsuk/scala-uri) library.

### Common Methods

#### `schemeOption`

Returns the URL scheme (protocol).

```scala
def schemeOption: Option[String]
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://example.com")
url.schemeOption // Some("https")
```

---

#### `host`

Returns the host component.

```scala
def host: Host
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://api.example.com/v1")
url.host // api.example.com
```

---

#### `port`

Returns the port number if specified.

```scala
def port: Option[Int]
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://example.com:8080")
url.port // Some(8080)
```

---

#### `path`

Returns the URL path.

```scala
def path: Path
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://example.com/api/v1/users")
url.path.toString // "/api/v1/users"
```

---

#### `query`

Returns the query parameters.

```scala
def query: QueryString
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://example.com?page=1&limit=10")
url.query.params // Vector(("page", Some("1")), ("limit", Some("10")))
```

---

#### `fragment`

Returns the URL fragment (anchor).

```scala
def fragment: Option[String]
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://example.com#section")
url.fragment // Some("section")
```

---

#### `toString`

Returns the string representation of the URL.

```scala
override def toString: String
```

**Example:**
```scala
val url = AbsoluteUrl.parse("https://example.com/path")
url.toString // "https://example.com/path"
```

---

## Host

Represents a URL host. Part of the [scala-uri](https://github.com/lemonlabsuk/scala-uri) library.

### Methods

#### `Host.parse`

Parses a string into a Host.

```scala
def parse(host: String): Host
```

**Parameters:**
- `host`: The host string to parse

**Returns:** A `Host` instance

**Example:**
```scala
import io.lemonlabs.uri.Host

val host = Host.parse("example.com")
val subdomain = Host.parse("api.example.com")
```

---

#### `toString`

Returns the string representation of the host.

```scala
override def toString: String
```

**Example:**
```scala
val host = Host.parse("example.com")
host.toString // "example.com"
```

---

## Type Aliases and Imports

### Required Imports

```scala
// Core detection classes
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

// URL types from scala-uri
import io.lemonlabs.uri.{AbsoluteUrl, Host}
```

### Common Patterns

#### Basic Detection

```scala
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.AbsoluteUrl

val detector = UrlDetector.default
val urls: Set[AbsoluteUrl] = detector.extract(text)
```

#### With Options

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

val detector = UrlDetector(UrlDetectorOptions.Html)
val urls = detector.extract(htmlContent)
```

#### With Host Filtering

```scala
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.{Host, AbsoluteUrl}

val detector = UrlDetector.default
  .withAllowed(Host.parse("example.com"))

val urls: Set[AbsoluteUrl] = detector.extract(text)
```

---

## Exception Handling

The library uses a fail-safe approach:

- **Invalid URLs**: Filtered out silently, not thrown as exceptions
- **Parsing Errors**: Caught internally, malformed URLs are excluded
- **No Exceptions**: `extract` method never throws exceptions

**Example:**
```scala
val detector = UrlDetector.default

// Even with invalid URLs, no exception is thrown
val urls = detector.extract("Invalid: ht!tp://bad-url, Valid: https://good.com")
// Returns: Set(https://good.com)
```

---

## Thread Safety

All components are immutable and thread-safe:

```scala
// Safe to share across threads
object SharedDetectors {
  val default: UrlDetector = UrlDetector.default
}

// Concurrent usage is safe
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val futures = texts.map { text =>
  Future {
    SharedDetectors.default.extract(text)
  }
}
```

---

## Return Types

All methods return immutable data structures:

- `extract` returns `Set[AbsoluteUrl]` (immutable, no duplicates)
- Builder methods (`withAllowed`, `withDenied`, `withOptions`) return new `UrlDetector` instances
- No mutable state is exposed

---

## Version Compatibility

The API is stable across:
- Scala 2.12.x
- Scala 2.13.x
- Scala 3.x

Binary compatibility maintained within major versions.
