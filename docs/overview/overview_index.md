---
id: overview_index
title: "Overview"
---

Scala URL Detector is a robust Scala library that detects and extracts URLs from unstructured text with support for multiple content formats. It is based on the fork of LinkedIn Engineering team's open-source library in the following [repository](https://github.com/URL-Detector/URL-Detector).

## Features

- **Multiple Detection Modes**: Support for HTML, XML, JSON, JavaScript, and plain text
- **Smart URL Parsing**: Handles URLs with or without schemes, protocol-relative URLs, and encoded characters
- **Host Filtering**: Allow or deny specific hosts with intelligent subdomain matching
- **Format-Aware Extraction**: Context-aware detection for different content types
- **IPv4 & IPv6 Support**: Recognizes both IPv4 and IPv6 addresses
- **Type-Safe API**: Uses [scala-uri](https://github.com/lemonlabsuk/scala-uri) for strongly-typed URL representations
- **Thread-Safe**: Immutable data structures safe for concurrent use
- **Cross-Platform**: Published for Scala 2.12, 2.13, and 3.x

## Installation

To use the latest release of Scala URL Detector in your project add the following to your `build.sbt` file:

```scala mdoc:passthrough
println(s"""```scala""")
println(s"""libraryDependencies += "${detection.BuildInfo.organization}" %% "${detection.BuildInfo.name}" % "${detection.BuildInfo.version.split('+').head}"""")
println(s"""```""")
```

## Quick Start

```scala
import io.lambdaworks.detection.UrlDetector
import io.lemonlabs.uri.AbsoluteUrl

// Basic usage
val detector = UrlDetector.default
val urls: Set[AbsoluteUrl] = detector.extract("Visit https://example.com")

// With specific options
import io.lambdaworks.detection.UrlDetectorOptions
val htmlDetector = UrlDetector(UrlDetectorOptions.Html)
val htmlUrls = htmlDetector.extract("<a href='https://example.com'>Link</a>")

// With host filtering
import io.lemonlabs.uri.Host
val filtered = UrlDetector.default
  .withAllowed(Host.parse("example.com"))
  .extract("Visit example.com and other.com")
```

## Documentation

### Getting Started
- [Usage Guide](overview_usage.md) - Basic usage and core concepts
- [Examples](overview_example.md) - Comprehensive examples for various use cases
- [Detection Options](overview_options.md) - Complete reference for all detection modes

### Advanced Topics
- [Advanced Usage](overview_advanced.md) - Advanced patterns and techniques
- [API Reference](overview_api.md) - Complete API documentation
- [Architecture & Design](overview_architecture.md) - Internal architecture and design principles

### Help & Support
- [FAQ & Troubleshooting](overview_faq.md) - Common questions and solutions

## Next Steps

- Read the [Usage Guide](overview_usage.md) to understand the core concepts
- Explore [Examples](overview_example.md) to see the library in action
- Check out [Detection Options](overview_options.md) to choose the right mode for your content
- Review the [API Reference](overview_api.md) for detailed method documentation