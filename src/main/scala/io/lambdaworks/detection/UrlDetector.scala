package io.lambdaworks.detection

import java.util.regex.Pattern

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import com.linkedin.urls.{Url => LUrl}
import org.apache.commons.lang3.StringUtils.endsWithAny
import org.apache.commons.validator.routines.DomainValidator

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

/** Represents URL detector.
  *
  *  @param content text from which URLs are being extracted
  *  @param config URL detector configuration
  */
final case class UrlDetector(content: String, config: Config) {

  private val allowlist: List[Url] = config.allowlist.map(Url.apply).map(sanitize(_))

  private val denylist: List[Url] = config.denylist.map(Url.apply).map(sanitize(_))

  val domainValidator: DomainValidator = DomainValidator.getInstance()

  private val detector: LUrlDetector =
    new LUrlDetector(sanitizeContent(content), LUrlDetectorOptions.valueOf(config.options.value))

  /** Method that extracts URLs from text.
    *
    *  @return list of found URLs
    */
  def extract(): List[Url] =
    if (config.options != UrlDetectorOptions.AllowSingleLevelDomain) {
      detector
        .detect()
        .asScala
        .toList
        .map(sanitize(_))
        .filter(checkIfValidDomain)
        .filter(allowlist.isEmpty || _.contained(allowlist))
        .filterNot(_.contained(denylist))
    } else {
      detector
        .detect()
        .asScala
        .toList
        .map(sanitize(_))
        .filter(allowlist.isEmpty || _.contained(allowlist))
        .filterNot(_.contained(denylist))
    }

  private def sanitize(url: String): Url = {
    @tailrec
    def loop(url: String): String =
      if (!endsWithAny(url, ",", "!", "-", ".", "`", "./")) url else loop(url.substring(0, url.length - 1))

    Url(LUrl.create(loop(url)))
  }

  private def sanitizeContent(content: String): String =
    content.replace("https://", " https://").replace("ftp://", " ftp://")

  private def checkIfValidDomain(url: Url): Boolean = {
    def getTld(url: Url): String =
      ".".concat(url.getHost.split("\\.").last)

    if (!Pattern.matches("\\.[0-9]+", getTld(url))) {
      domainValidator.isValidTld(getTld(url))
    } else true
  }

}
