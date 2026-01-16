![scala-version][scala-version-badge]
[![Scala CI](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml/badge.svg)](https://github.com/lambdaworks/scurl-detector/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Release Version][sonatype-releases-badge]][sonatype-releases-version]
[![Snapshot Version][sonatype-snapshots-badge]][sonatype-snapshots-version]

# Scala URL Detector

A robust Scala library that detects and extracts URLs from unstructured text with support for multiple content formats.

Based on [LinkedIn Engineering's URL Detector](https://github.com/URL-Detector/URL-Detector), this library provides a type-safe, functional Scala API for extracting URLs from text in various formats including HTML, XML, JSON, and JavaScript.

## Features

- **Multiple Detection Modes**: Support for HTML, XML, JSON, JavaScript, and plain text
- **Smart URL Parsing**: Handles URLs with or without schemes, protocol-relative URLs, and encoded characters
- **Host Filtering**: Allow or deny specific hosts with intelligent subdomain matching
- **Format-Aware Extraction**: Context-aware detection for different content types (quotes, brackets, delimiters)
- **IPv4 & IPv6 Support**: Recognizes both IPv4 and IPv6 addresses
- **TLD Validation**: Validates URLs against public suffix lists
- **Email Filtering**: Automatically excludes email addresses from detection
- **Type-Safe API**: Uses [scala-uri](https://github.com/lemonlabsuk/scala-uri) for strongly-typed URL representations
- **Cross-Platform**: Published for Scala 2.12, 2.13, and 3.x

## Quick Start

Add the following to your `build.sbt`:

```scala
libraryDependencies += "io.lambdaworks" %% "scurl-detector" % "version"
```

### Basic Usage

```scala
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.AbsoluteUrl

// Use default detector
val detector = UrlDetector.default
val urls: Set[AbsoluteUrl] = detector.extract("Check out https://example.com and lambdaworks.io")

// Use with specific options
import io.lambdaworks.detection.UrlDetectorOptions

val jsonDetector = UrlDetector(UrlDetectorOptions.Json)
val extractedUrls = jsonDetector.extract("""{"url": "https://api.example.com/v1"}""")

// Filter by allowed hosts
import io.lemonlabs.uri.Host

val filtered = UrlDetector.default
  .withAllowed(Host.parse("lambdaworks.io"))
  .extract("Visit lambdaworks.io and example.com")  // Only returns lambdaworks.io
```

## Detection Options

The library supports 9 different detection modes optimized for various content types:

- **Default**: Basic URL detection
- **QuoteMatch**: Handles double-quoted URLs
- **SingleQuoteMatch**: Handles single-quoted URLs
- **BracketMatch**: Handles URLs in brackets/parentheses
- **Json**: Optimized for JSON content
- **Javascript**: Optimized for JavaScript code
- **Xml**: Optimized for XML documents
- **Html**: Optimized for HTML content
- **AllowSingleLevelDomain**: Allows single-level domains like `http://localhost`

See the [full documentation](https://lambdaworks.github.io/scurl-detector/) for detailed information about each option.

## Documentation

- [Full Documentation](https://lambdaworks.github.io/scurl-detector/)
- [API Reference](https://lambdaworks.github.io/scurl-detector/overview/overview_usage/)
- [Examples](https://lambdaworks.github.io/scurl-detector/overview/overview_example/)
- [Contribution Guidelines](https://lambdaworks.github.io/scurl-detector/contributing/)
- [Code of Conduct](https://lambdaworks.github.io/scurl-detector/code-of-conduct/)

## Contributing

We welcome contributions! Please see our [Contribution Guidelines](https://lambdaworks.github.io/scurl-detector/contributing/) for details.

## License

This project is licensed under [Apache 2.0](LICENSE).

[scala-version-badge]: https://img.shields.io/badge/scala-2.13.16-blue?logo=scala&color=red

[sonatype-releases-badge]: https://img.shields.io/maven-central/v/io.lambdaworks/scurl-detector_2.13.svg?label=Release "Maven Central"
[sonatype-releases-version]: https://central.sonatype.com/artifact/io.lambdaworks/scurl-detector_2.13 "Maven Central"
[sonatype-snapshots-badge]: https://img.shields.io/maven-metadata/v?label=Snapshot&metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Flambdaworks%2Fscurl-detector_2.13%2Fmaven-metadata.xml "Sonatype Snapshots"
[sonatype-snapshots-version]: https://central.sonatype.com/repository/maven-snapshots/io/lambdaworks/scurl-detector_2.13/ "Sonatype Snapshots"
