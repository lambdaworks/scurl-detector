package io.lambdaworks.detection

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import com.linkedin.urls.{Url => LUrl}
import org.apache.commons.lang3.StringUtils.endsWithAny

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

final case class UrlDetector(content: String, options: UrlDetectorOptions = UrlDetectorOptions.Default) {

  private val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(options.value))

  def extract(): List[Url] = detector.detect().asScala.toList.map(sanitize(_))

  private def sanitize(url: String): Url = {
    @tailrec
    def loop(url: String): String =
      if (!endsWithAny(url, ",", "!", "-", ".", "`")) url else loop(url.substring(0, url.length - 1))

    Url(LUrl.create(loop(url)))
  }

}
