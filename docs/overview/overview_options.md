---
id: overview_options
title: "Detection Options"
---

## Overview

`UrlDetectorOptions` control how the URL detector handles different text formats and delimiters. Each option is optimized for specific content types to ensure accurate URL extraction while avoiding false positives.

## Available Options

### Default

Basic URL detection without special delimiter handling.

**Use case**: Plain text where URLs are clearly separated by whitespace or punctuation.

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

val detector = UrlDetector(UrlDetectorOptions.Default)
val urls = detector.extract("Visit https://example.com for more info")
```

### QuoteMatch

Strips matching double quotes from URL boundaries.

**Use case**: Text containing URLs wrapped in double quotes.

```scala
val detector = UrlDetector(UrlDetectorOptions.QuoteMatch)

// Correctly extracts https://example.com (without quotes)
val urls = detector.extract("""The URL is "https://example.com" in the document""")
```

**Behavior**:
- `"https://example.com"` → extracts `https://example.com`
- `"https://example.com` → extracts `"https://example.com` (non-matching quote retained)

### SingleQuoteMatch

Strips matching single quotes from URL boundaries.

**Use case**: Text containing URLs wrapped in single quotes, common in command-line documentation.

```scala
val detector = UrlDetector(UrlDetectorOptions.SingleQuoteMatch)

// Correctly extracts https://api.example.com (without quotes)
val urls = detector.extract("Run: curl 'https://api.example.com/v1'")
```

**Behavior**:
- `'https://example.com'` → extracts `https://example.com`
- `'https://example.com` → extracts `'https://example.com` (non-matching quote retained)

### BracketMatch

Handles bracket characters: parentheses `()`, curly braces `{}`, and square brackets `[]`.

**Use case**: Text where URLs are enclosed in brackets, common in Markdown and documentation.

```scala
val detector = UrlDetector(UrlDetectorOptions.BracketMatch)

// Extracts URLs without surrounding brackets
val urls = detector.extract("""
  See (https://example.com) for details.
  Configuration: {https://config.example.com}
  Reference: [https://docs.example.com]
""")
```

**Behavior**:
- `(https://example.com)` → extracts `https://example.com`
- `{https://example.com}` → extracts `https://example.com`
- `[https://example.com]` → extracts `https://example.com`
- Handles nested brackets appropriately

### Json

Optimized for JSON-formatted content. Checks for bracket characters and double quotes.

**Use case**: Extracting URLs from JSON documents, API responses, or configuration files.

```scala
val detector = UrlDetector(UrlDetectorOptions.Json)

val jsonContent = """{
  "homepage": "https://example.com",
  "api": "https://api.example.com/v1",
  "docs": ["https://docs.example.com", "https://help.example.com"]
}"""

val urls = detector.extract(jsonContent)
```

**Behavior**:
- Handles JSON string escaping
- Respects JSON structural characters (brackets, quotes, commas)
- Extracts URLs without JSON delimiters

### Javascript

Optimized for JavaScript code. Combines JSON rules with single quote handling.

**Use case**: Extracting URLs from JavaScript source code, scripts, or template literals.

```scala
val detector = UrlDetector(UrlDetectorOptions.Javascript)

val jsCode = """
  const API_URL = 'https://api.example.com';
  fetch("https://data.example.com/users")
    .then(response => response.json())
"""

val urls = detector.extract(jsCode)
```

**Behavior**:
- Handles both single and double quoted strings
- Respects JavaScript syntax (brackets, quotes, semicolons)
- Works with template literals and string concatenation

### Xml

Optimized for XML documents. Checks XML characters and uses them as URL delimiters, includes quote matching.

**Use case**: Extracting URLs from XML documents, SVG files, or XML-based configuration.

```scala
val detector = UrlDetector(UrlDetectorOptions.Xml)

val xmlContent = """<?xml version="1.0"?>
<config>
  <endpoint url="https://api.example.com/v1"/>
  <link>https://docs.example.com</link>
  <resource href="https://cdn.example.com/assets"/>
</config>"""

val urls = detector.extract(xmlContent)
```

**Behavior**:
- Respects XML tag boundaries `<` and `>`
- Handles XML attribute syntax
- Processes CDATA sections appropriately
- Manages XML entity references

### Html

Optimized for HTML content. Checks all rules except brackets, useful for HTML with embedded JavaScript.

**Use case**: Extracting URLs from HTML documents, web scraping, or processing web content.

```scala
val detector = UrlDetector(UrlDetectorOptions.Html)

val htmlContent = """
<html>
  <head><link rel="stylesheet" href="https://cdn.example.com/style.css"></head>
  <body>
    <a href="https://example.com">Visit our site</a>
    <img src="https://example.com/logo.png" alt="Logo">
    <script>window.location = "https://redirect.example.com";</script>
  </body>
</html>"""

val urls = detector.extract(htmlContent)
```

**Behavior**:
- Handles HTML tag syntax
- Processes both single and double quoted attributes
- Works with embedded JavaScript
- Respects HTML entities
- Does not treat brackets as delimiters (to handle JavaScript functions in onclick handlers)

### AllowSingleLevelDomain

Allows detection of single-level domains without public suffixes.

**Use case**: Internal networks, localhost development, or custom TLDs.

```scala
val detector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)

val text = """
  Development: http://localhost:8080
  Internal: http://intranet
  Go link: go/docs
"""

val urls = detector.extract(text)
```

**Behavior**:
- Allows `http://localhost`, `https://localhost:3000`
- Allows internal domain names without public TLDs
- Allows shortcut URLs like `go/docs`, `goto/project`
- Still validates format and structure
- Can be combined with other options

## Combining Options

Options can be used in combination with host filtering:

```scala
val detector = UrlDetector(UrlDetectorOptions.Html)
  .withAllowed(Host.parse("example.com"))
  .withDenied(Host.parse("ads.example.com"))

// Only extracts URLs from example.com, excluding ads.example.com subdomain
val urls = detector.extract(htmlContent)
```

## Choosing the Right Option

| Content Type | Recommended Option | Why |
|--------------|-------------------|-----|
| Plain text | `Default` | Simple, no special delimiter handling needed |
| JSON API responses | `Json` | Handles JSON syntax and escaping |
| JavaScript code | `Javascript` | Handles both quote types and JS syntax |
| XML/SVG files | `Xml` | Respects XML structure and entities |
| HTML pages | `Html` | Handles HTML tags and embedded scripts |
| Documentation/Markdown | `BracketMatch` | Common to have URLs in brackets |
| Shell scripts | `SingleQuoteMatch` | Shell scripts often use single quotes |
| Config files with quotes | `QuoteMatch` | Many configs use double quotes |
| Development/Testing | `AllowSingleLevelDomain` | Allows localhost and internal domains |

## Performance Considerations

- All options have similar performance characteristics
- Choose the most specific option for your content type to reduce false positives
- Host filtering is applied after URL extraction, so it doesn't impact initial detection performance
