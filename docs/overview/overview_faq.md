---
id: overview_faq
title: "FAQ & Troubleshooting"
---

## Frequently Asked Questions

### General Questions

#### Q: What is Scala URL Detector?

A: Scala URL Detector is a library that extracts URLs from unstructured text. It's built on LinkedIn's URL Detector with a functional, type-safe Scala API using scala-uri for URL representation.

---

#### Q: Which Scala versions are supported?

A: The library supports Scala 2.12.x, 2.13.x, and 3.x with full cross-compilation.

---

#### Q: Is this library thread-safe?

A: Yes, all components are immutable and thread-safe. You can safely share `UrlDetector` instances across multiple threads.

---

#### Q: What license is this library under?

A: Apache License 2.0

---

### Usage Questions

#### Q: Why are some URLs not being detected?

**Possible reasons:**

1. **Invalid URL format**: The URL doesn't conform to valid URL syntax
   ```scala
   // Won't be detected - invalid format
   val urls = detector.extract("Invalid: ht!tp://bad")
   ```

2. **Email addresses**: Emails are automatically filtered out
   ```scala
   // Won't be detected - looks like email
   val urls = detector.extract("Contact: user@example.com")
   ```

3. **Missing TLD**: URLs without valid top-level domains are rejected
   ```scala
   // Won't be detected - no valid TLD
   val urls = UrlDetector.default.extract("http://notatld")

   // Solution: Use AllowSingleLevelDomain
   val urls = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
     .extract("http://localhost")
   ```

4. **Host filtering**: The URL is excluded by `withAllowed` or `withDenied`
   ```scala
   val detector = UrlDetector.default.withAllowed(Host.parse("example.com"))
   // Won't detect other.com
   val urls = detector.extract("Visit other.com")
   ```

---

#### Q: Why is my URL extracted with `http://` instead of `https://`?

A: URLs without explicit schemes are assigned `http://` by default. Always include the scheme in your URLs:

```scala
val detector = UrlDetector.default

// Without scheme - gets http://
detector.extract("example.com") // Returns: http://example.com

// With scheme - preserved
detector.extract("https://example.com") // Returns: https://example.com
```

**Post-filtering solution:**
```scala
val allUrls = detector.extract(text)
val httpsOnly = allUrls.filter(_.schemeOption.contains("https"))
```

---

#### Q: How do I detect localhost URLs?

A: Use the `AllowSingleLevelDomain` option:

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

val detector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
val urls = detector.extract("Development: http://localhost:8080")
// Successfully detects: http://localhost:8080
```

---

#### Q: Can I extract URLs from multiple formats in one pass?

A: No, each detector uses a single detection mode. For multiple formats, create separate detectors or process in multiple passes:

```scala
val htmlUrls = UrlDetector(UrlDetectorOptions.Html).extract(text)
val jsonUrls = UrlDetector(UrlDetectorOptions.Json).extract(text)
val allUrls = htmlUrls ++ jsonUrls
```

---

#### Q: How do I handle relative URLs?

A: The library only extracts absolute URLs. Relative URLs are not detected. If you need to handle relative URLs, you'll need to convert them to absolute URLs first:

```scala
import io.lemonlabs.uri.{AbsoluteUrl, RelativeUrl}

val baseUrl = AbsoluteUrl.parse("https://example.com")
val relativeUrl = RelativeUrl.parse("/api/v1")
val absoluteUrl = baseUrl.resolve(relativeUrl)
```

---

#### Q: Why are my bracket/quote characters not being stripped?

A: You need to use the appropriate detection option:

```scala
// Wrong - Default doesn't handle brackets
val detector1 = UrlDetector.default
detector1.extract("(https://example.com)") // Includes parentheses

// Correct - Use BracketMatch
val detector2 = UrlDetector(UrlDetectorOptions.BracketMatch)
detector2.extract("(https://example.com)") // https://example.com

// For quotes - use QuoteMatch or SingleQuoteMatch
val detector3 = UrlDetector(UrlDetectorOptions.QuoteMatch)
detector3.extract("\"https://example.com\"") // https://example.com
```

---

### Host Filtering Questions

#### Q: How does subdomain matching work?

A: When you specify a host, all subdomains are automatically matched:

```scala
import io.lemonlabs.uri.Host

val detector = UrlDetector.default.withAllowed(Host.parse("example.com"))

// All of these match:
// - example.com
// - www.example.com
// - api.example.com
// - any.subdomain.example.com
```

---

#### Q: How do I allow only the apex domain (no subdomains)?

A: The library doesn't support apex-only matching. Workaround using post-filtering:

```scala
val detector = UrlDetector.default.withAllowed(Host.parse("example.com"))
val urls = detector.extract(text)

// Filter to apex domain only
val apexOnly = urls.filter { url =>
  val host = url.host.toString
  host == "example.com" || host == "www.example.com"
}
```

---

#### Q: What happens when a URL matches both allowed and denied hosts?

A: Denied hosts take precedence:

```scala
val detector = UrlDetector.default
  .withAllowed(Host.parse("example.com"))
  .withDenied(Host.parse("ads.example.com"))

detector.extract("Visit ads.example.com")
// Returns: empty Set (denied takes precedence)
```

---

#### Q: Can I use wildcards in host filtering?

A: No direct wildcard support, but subdomain matching provides similar functionality. For more complex patterns, use post-filtering:

```scala
val urls = UrlDetector.default.extract(text)

// Custom filtering with regex
val pattern = ".*\\.cdn\\.example\\.com".r
val cdnUrls = urls.filter { url =>
  pattern.matches(url.host.toString)
}
```

---

### Performance Questions

#### Q: Is it better to create one detector or multiple?

A: Reuse detector instances when possible:

```scala
// Good - reuse instance
val detector = UrlDetector(UrlDetectorOptions.Html)
val results = documents.map(doc => detector.extract(doc))

// Less efficient - creates new instance each time
val results = documents.map { doc =>
  UrlDetector(UrlDetectorOptions.Html).extract(doc)
}
```

---

#### Q: How do I process large documents efficiently?

A: Consider chunking and parallel processing:

```scala
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

def extractParallel(largeText: String, chunkSize: Int = 10000): Future[Set[AbsoluteUrl]] = {
  val chunks = largeText.grouped(chunkSize).toSeq
  val detector = UrlDetector.default

  val futures = chunks.map(chunk => Future(detector.extract(chunk)))
  Future.sequence(futures).map(_.flatten.toSet)
}
```

---

#### Q: Does host filtering impact performance?

A: Host filtering is applied after URL extraction, so it has minimal impact. The overhead is O(m × h) where m is the number of detected URLs and h is the number of filter hosts.

---

### Integration Questions

#### Q: How do I use this with Cats Effect or ZIO?

A: The library is synchronous but can be easily lifted:

**Cats Effect:**
```scala
import cats.effect.IO
import io.lambdaworks.detection.UrlDetector

def extractIO(text: String): IO[Set[AbsoluteUrl]] = {
  IO(UrlDetector.default.extract(text))
}
```

**ZIO:**
```scala
import zio._
import io.lambdaworks.detection.UrlDetector

def extractZIO(text: String): Task[Set[AbsoluteUrl]] = {
  ZIO.attempt(UrlDetector.default.extract(text))
}
```

---

#### Q: Can I use this with Akka Streams or FS2?

A: Yes, you can integrate it into stream processing:

**FS2:**
```scala
import fs2.Stream
import cats.effect.IO
import io.lambdaworks.detection.UrlDetector

val detector = UrlDetector.default

val pipeline: Stream[IO, Set[AbsoluteUrl]] =
  Stream("text1", "text2", "text3")
    .evalMap(text => IO(detector.extract(text)))
```

**Akka Streams:**
```scala
import akka.stream.scaladsl._
import io.lambdaworks.detection.UrlDetector

val detector = UrlDetector.default

val flow: Flow[String, Set[AbsoluteUrl], NotUsed] =
  Flow[String].map(text => detector.extract(text))
```

---

## Troubleshooting

### Issue: `NoClassDefFoundError` or `ClassNotFoundException`

**Cause:** Missing dependencies in classpath

**Solution:** Ensure all dependencies are included:

```scala
// build.sbt
libraryDependencies ++= Seq(
  "io.lambdaworks" %% "scurl-detector" % "version"
)
```

For manual classpath setup, ensure these are included:
- scurl-detector
- scala-uri
- url-detector (LinkedIn's Java library)
- commons-validator

---

### Issue: URLs in JSON not detected correctly

**Cause:** Using wrong detection option

**Solution:** Use `UrlDetectorOptions.Json`:

```scala
// Wrong
val urls = UrlDetector.default.extract(jsonString)

// Correct
val urls = UrlDetector(UrlDetectorOptions.Json).extract(jsonString)
```

---

### Issue: Too many false positives

**Solutions:**

1. **Use appropriate detection option** for your content type
2. **Apply host filtering** to limit results
3. **Post-filter** results with custom validation:

```scala
def isValidUrl(url: AbsoluteUrl): Boolean = {
  val validSchemes = Set("http", "https")
  val hasValidScheme = url.schemeOption.exists(validSchemes.contains)
  val hasStandardPort = url.port.forall(p => p == 80 || p == 443 || p == 8080)

  hasValidScheme && hasStandardPort
}

val urls = detector.extract(text).filter(isValidUrl)
```

---

### Issue: IPv6 URLs not detected

**Cause:** IPv6 URLs need proper bracket notation

**Solution:** Ensure IPv6 addresses are properly formatted:

```scala
// Correct format
val urls = detector.extract("http://[2001:db8::1]:8080/")

// Incorrect format (won't be detected)
val urls = detector.extract("http://2001:db8::1:8080/")
```

---

### Issue: Internal/intranet URLs not detected

**Cause:** Single-level domains are rejected by default

**Solution:** Use `AllowSingleLevelDomain`:

```scala
val detector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)
val urls = detector.extract("http://intranet/docs")
```

---

### Issue: Memory usage is high with large documents

**Solutions:**

1. **Process in chunks:**
```scala
val chunkSize = 10000
val chunks = largeText.grouped(chunkSize)
val allUrls = chunks.flatMap(chunk => detector.extract(chunk)).toSet
```

2. **Stream processing:**
```scala
import scala.io.Source
val urls = Source.fromFile("large.txt")
  .getLines()
  .flatMap(line => detector.extract(line))
  .toSet
```

3. **Limit result size** with filtering

---

### Issue: Getting `java.net.MalformedURLException`

**Cause:** This shouldn't happen - the library catches parsing exceptions

**If you see this:**
1. Check if you're directly using scala-uri's parse methods
2. Ensure you're using the library's `extract` method
3. Report as a bug if it's coming from `extract`

```scala
// Safe - exceptions are caught internally
val urls = detector.extract(text)

// Unsafe - can throw exceptions
val url = AbsoluteUrl.parse(possiblyInvalidUrl)

// Safe alternative
Try(AbsoluteUrl.parse(possiblyInvalidUrl)).toOption
```

---

## Common Patterns

### Pattern: Validating User Input

```scala
def extractSafeUrls(userInput: String): Set[AbsoluteUrl] = {
  val detector = UrlDetector(UrlDetectorOptions.Default)
    .withDenied(
      Host.parse("localhost"),
      Host.parse("127.0.0.1"),
      Host.parse("0.0.0.0")
    )

  detector.extract(userInput)
    .filter(_.schemeOption.exists(s => Set("http", "https").contains(s)))
}
```

---

### Pattern: Extracting APIs from Documentation

```scala
def extractApiUrls(docs: String): Set[AbsoluteUrl] = {
  UrlDetector(UrlDetectorOptions.BracketMatch)
    .extract(docs)
    .filter(_.path.toString().contains("/api/"))
}
```

---

### Pattern: Building a URL Allow List

```scala
val allowedDomains = Set("example.com", "api.example.com", "cdn.example.com")

val detector = allowedDomains.foldLeft(UrlDetector.default) { (det, domain) =>
  det.withAllowed(Host.parse(domain))
}
```

---

## Getting Help

If you can't find the answer here:

1. Check the [examples documentation](overview_example.md)
2. Review the [API reference](overview_api.md)
3. Search [GitHub Issues](https://github.com/lambdaworks/scurl-detector/issues)
4. Open a new issue with:
   - Scala version
   - Library version
   - Minimal reproducible example
   - Expected vs actual behavior

---

## Reporting Bugs

When reporting bugs, please include:

1. **Scala version:** (e.g., 2.13.12)
2. **Library version:** (e.g., scurl-detector 1.0.0)
3. **Minimal code example:**
```scala
val detector = UrlDetector.default
val result = detector.extract("your test input")
println(result) // What you got
// Expected: what you expected
```
4. **Error messages** (if any)
5. **Environment details** (OS, JVM version)
