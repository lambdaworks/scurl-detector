---
id: overview_architecture
title: "Architecture & Design"
---

## Overview

Scala URL Detector is built on top of LinkedIn's URL Detector library, providing a functional, type-safe Scala API for extracting URLs from unstructured text. The library is designed with immutability, composability, and type safety as core principles.

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        User Code                             │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    UrlDetector API                           │
│  (Immutable, Type-Safe Scala Interface)                     │
│                                                               │
│  • UrlDetector.extract(text: String)                        │
│  • withAllowed/withDenied host filtering                    │
│  • UrlDetectorOptions configuration                         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              URL Detection Pipeline                          │
│                                                               │
│  1. Preprocess special characters                           │
│  2. Detect URL candidates (LinkedIn detector)               │
│  3. Normalize URLs (encoding, protocols)                    │
│  4. Parse and validate URLs (scala-uri)                     │
│  5. Apply filtering (allowed/denied hosts)                  │
│  6. Validate structure (TLD, email rejection, etc.)         │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                 Set[AbsoluteUrl]                             │
│            (Validated, Parsed URLs)                          │
└─────────────────────────────────────────────────────────────┘
```

### Core Components

#### 1. UrlDetector

The main entry point and orchestrator for URL extraction.

**Responsibilities:**
- Configure detection options
- Manage host filtering (allowed/denied lists)
- Coordinate the detection pipeline
- Ensure immutability through builder pattern

**Key Design:**
- Immutable: All modifications return new instances
- Composable: Methods can be chained fluently
- Thread-safe: Safe to share across threads

```scala
case class UrlDetector(
  options: UrlDetectorOptions,
  allowedHosts: Option[NonEmptySet[Host]] = None,
  deniedHosts: Set[Host] = Set.empty
)
```

#### 2. UrlDetectorOptions

A sealed trait representing detection modes optimized for different content types.

**Design Pattern:** Sum Type (Sealed Trait + Case Objects)

**Benefits:**
- Compile-time exhaustiveness checking
- Type-safe option selection
- Clear, discoverable API

**Implementation:**
```scala
sealed trait UrlDetectorOptions

object UrlDetectorOptions {
  case object Default extends UrlDetectorOptions
  case object QuoteMatch extends UrlDetectorOptions
  case object SingleQuoteMatch extends UrlDetectorOptions
  case object BracketMatch extends UrlDetectorOptions
  case object Json extends UrlDetectorOptions
  case object Javascript extends UrlDetectorOptions
  case object Xml extends UrlDetectorOptions
  case object Html extends UrlDetectorOptions
  case object AllowSingleLevelDomain extends UrlDetectorOptions
}
```

#### 3. URL Representation

Uses [scala-uri](https://github.com/lemonlabsuk/scala-uri) for type-safe URL representation.

**Why scala-uri:**
- Type-safe parsing and construction
- Rich API for URL manipulation
- Immutable data structures
- Well-tested and maintained

**Key Types:**
- `AbsoluteUrl`: Fully qualified URLs with scheme and host
- `Host`: Represents URL hosts with subdomain handling
- URL components accessible via methods (path, query, fragment, etc.)

## Detection Pipeline

### Step-by-Step Processing

#### 1. Preprocessing

Before URL detection, the text is preprocessed to handle special characters:

```scala
// Handles URLs prefixed with special characters like #, @, !, $, ~, *
// Example: "#https://example.com" → "https://example.com"
private val SpecialCharPrefixPattern = "([@#!$~*]+)(.*)"
```

#### 2. Candidate Detection

Uses LinkedIn's URL Detector (Java library) to identify potential URLs:

- Leverages battle-tested URL detection logic
- Handles various URL formats and edge cases
- Language-agnostic detection algorithm

#### 3. Normalization

Normalizes detected URLs:

**Encoded Spaces:**
```scala
// Converts %20 to spaces for easier processing
url.replace("%20", " ")
```

**Protocol-Relative URLs:**
```scala
// Converts //example.com → http://example.com
if (url.startsWith("//")) s"http:$url"
```

**Default Scheme:**
```scala
// Adds http:// if no scheme present
if (!hasScheme(url)) s"http://$url"
```

#### 4. Parsing and Validation

Uses scala-uri to parse URLs with error handling:

```scala
Try(AbsoluteUrl.parse(normalizedUrl)) match {
  case Success(url) => Some(url)
  case Failure(_) => None  // Gracefully skip invalid URLs
}
```

**Validation Checks:**
- URL structure validity
- Host format validation
- Port number validation (if present)
- Path encoding validation

#### 5. Host Filtering

Applies allowed/denied host filtering with intelligent subdomain matching:

**Matching Rules:**
- Apex domain matching: `example.com` matches `www.example.com`, `api.example.com`
- www is implicitly assumed
- Explicit subdomain matching available
- Denied hosts take precedence over allowed hosts

**Implementation:**
```scala
def matchesHost(url: AbsoluteUrl, host: Host): Boolean = {
  val urlHost = url.host
  urlHost == host ||
  urlHost.toString.endsWith(s".${host}") ||
  urlHost.toString == s"www.${host}"
}
```

#### 6. Additional Validation

**Email Rejection:**
```scala
// Uses Apache Commons Validator
!EmailValidator.getInstance().isValid(urlString)
```

**TLD Validation:**
- Validates against public suffix list
- Ensures URLs have valid top-level domains
- Can be bypassed with `AllowSingleLevelDomain` option

**Userinfo Validation:**
- URLs with user credentials must have explicit schemes
- Prevents `user:pass@example.com` from being detected (could be email)
- Allows `ftp://user:pass@example.com`

**Special Character Cleanup:**
```scala
// Removes leading/trailing special characters
private val SanitizeRegex = "^[@#!$,\\-`.~*/]+|[@#!$,\\-`.~*/]+$"
```

## Design Principles

### 1. Immutability

All data structures are immutable:

```scala
// Returns a NEW detector instance
def withAllowed(host: Host, hosts: Host*): UrlDetector = {
  copy(allowedHosts = Some(NonEmptySet.of(host, hosts: _*)))
}
```

**Benefits:**
- Thread-safe by default
- Easier to reason about
- No defensive copying needed
- Referential transparency

### 2. Type Safety

Leverages Scala's type system:

```scala
// Sealed trait ensures all options are known at compile time
sealed trait UrlDetectorOptions

// Distinct types for different URL categories
trait AbsoluteUrl  // Has scheme and host
trait RelativeUrl  // Missing scheme or host
```

**Benefits:**
- Compile-time guarantees
- IDE autocomplete and navigation
- Refactoring safety

### 3. Composability

Fluent API design for easy composition:

```scala
val detector = UrlDetector(UrlDetectorOptions.Html)
  .withAllowed(Host.parse("example.com"))
  .withDenied(Host.parse("ads.example.com"))
  .extract(htmlContent)
```

**Benefits:**
- Readable, declarative code
- Easy to build complex configurations
- Natural expression of intent

### 4. Fail-Safe

Graceful error handling throughout:

```scala
// Invalid URLs are filtered out, not thrown as exceptions
Try(AbsoluteUrl.parse(url)) match {
  case Success(parsed) => Some(parsed)
  case Failure(_) => None
}
```

**Benefits:**
- No unexpected exceptions during extraction
- Robust against malformed input
- Predictable behavior

### 5. Performance

Performance considerations:

- **Lazy Initialization**: Default detector created lazily
- **Efficient Collections**: Uses `Set` for O(1) lookups and deduplication
- **Minimal Allocations**: Reuses detector instances
- **Regex Compilation**: Patterns compiled once at class load

```scala
object UrlDetector {
  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default)
}
```

## Dependencies

### Core Dependencies

**LinkedIn URL Detector (0.1.23)**
- Provides core URL detection algorithm
- Java library with proven detection logic
- Handles complex URL patterns and edge cases

**scala-uri (4.2.0)**
- Type-safe URL parsing and manipulation
- Rich API for URL components
- Well-tested URL handling

**Apache Commons Validator (1.10.1)**
- Email validation
- Domain validation
- IP address validation

**Cats (NonEmptySet)**
- Type-safe non-empty collections
- Used for allowed hosts (ensures at least one host)
- Functional programming utilities

### Cross-Compilation

Supports Scala 2.12, 2.13, and 3.x:

```scala
// build.sbt
crossScalaVersions := Seq("2.12.21", "2.13.18", "3.7.4")
```

**Compatibility:**
- Uses scala-collection-compat for cross-version compatibility
- Tested against all supported Scala versions
- Binary compatible within major versions

## Testing Strategy

Comprehensive test coverage using ScalaTest:

```scala
class UrlDetectorSpec extends AnyWordSpec with Matchers {
  // Tests for each detection option
  // Tests for host filtering
  // Tests for edge cases (IPv6, encoded URLs, etc.)
  // Tests for error handling
}
```

**Test Categories:**
1. Detection option behavior
2. Host filtering logic
3. URL normalization
4. Edge cases and malformed input
5. Integration scenarios

## Extension Points

The library can be extended:

### Custom Validation

```scala
def customExtract(text: String): Set[AbsoluteUrl] = {
  val urls = UrlDetector.default.extract(text)
  urls.filter(customValidation)
}

def customValidation(url: AbsoluteUrl): Boolean = {
  // Your custom validation logic
}
```

### Custom Detectors

```scala
object CustomDetectors {
  lazy val intranet: UrlDetector =
    UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
      .withAllowed(Host.parse("corp"))

  lazy val production: UrlDetector =
    UrlDetector(UrlDetectorOptions.Html)
      .withDenied(
        Host.parse("localhost"),
        Host.parse("127.0.0.1")
      )
}
```

## Future Considerations

Potential areas for enhancement:

1. **Async API**: Support for asynchronous URL extraction
2. **Streaming**: Process large documents as streams
3. **Custom Validators**: Plugin architecture for validation rules
4. **Caching**: Optional caching for repeated extractions
5. **Metrics**: Built-in performance metrics and monitoring
6. **Custom TLD Lists**: Allow custom public suffix lists

## Performance Characteristics

**Time Complexity:**
- Detection: O(n) where n is text length
- Host filtering: O(m × h) where m is number of URLs, h is number of hosts
- Overall: O(n + m × h)

**Space Complexity:**
- O(m) where m is number of detected URLs
- Set deduplication: O(m) space for unique URLs

**Optimization Tips:**
1. Reuse detector instances (avoid creating new ones)
2. Apply host filters to reduce result set size
3. Use specific detection options to reduce false positives
4. For large texts, consider parallel processing
