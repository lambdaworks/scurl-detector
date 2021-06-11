package io.lambdaworks.detection

import scala.jdk.CollectionConverters._
import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}

final case class UrlDetector(content: String, options: UrlDetectorOptions = UrlDetectorOptions.Default) {

  private val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(options.value))

  def extract(): List[Url] = detector.detect().asScala.toList.map(Url.apply)

}
