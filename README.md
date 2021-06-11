# Scala URL Detector

![scala-version][scala-version-badge]

Scala library that detects and extracts URLs from text. It is created to overcome some of the known issues of LinkedIn Engineering team's open sourced https://github.com/linkedin/URL-Detector.


### How to use
To use Scala URL Detector library, import the UrlDetector class and instantiate it with text in which you want to extract URLs from and UrlDetectorOptions you prefer for that text.

```scala
import io.lambdaworks.detection.{UrlDetector, UrlDetectorOptions}

val detector = UrlDetector("hello this is a url Linkedin.com", UrlDetectorOptions.Default)
val foundUrls: List[Url] = detector.extract()
for (url <- foundUrls) println(url)
```
---
## About:
The original Java library was written by the security team. Some of the primary authors are:
* Vlad Shlosberg (vshlosbe@linkedin.com)
* Tzu-Han Jan (tjan@linkedin.com)
* Yulia Astakhova (jastakho@linkedin.com)
---
## License
Original Java code is Copyright 2015 LinkedIn Corp. All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the license at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

[scala-version-badge]: https://img.shields.io/badge/scala-2.13.6-blue?logo=scala&color=teal
