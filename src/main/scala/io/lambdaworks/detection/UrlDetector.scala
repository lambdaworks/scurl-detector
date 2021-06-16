package io.lambdaworks.detection

import scala.jdk.CollectionConverters._
import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import com.linkedin.urls.{Url => LUrl}
import org.apache.commons.lang3.StringUtils.endsWithAny

import scala.annotation.tailrec

final case class UrlDetector(content: String, options: UrlDetectorOptions = UrlDetectorOptions.Default) {

  private val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(options.value))

  def extract(): List[Url] = detector.detect().asScala.toList.map(Url.apply).map(sanitize)

  private def sanitize(url: Url): Url = {
    @tailrec
    def loop(urlString: String): String =
      if (!endsWithAny(urlString, ",", "!", "-", ".", "`")) urlString
      else loop(urlString.substring(0, urlString.length - 1))
    Url(LUrl.create(loop(url.toString)))
  }

}
