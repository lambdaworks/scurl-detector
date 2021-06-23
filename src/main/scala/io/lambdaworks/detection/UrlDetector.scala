package io.lambdaworks.detection

import java.util.regex.Pattern

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import com.linkedin.urls.{Url => LUrl}
import org.apache.commons.lang3.StringUtils.endsWithAny
import org.apache.commons.validator.routines.DomainValidator

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

final case class UrlDetector(content: String, config: Config) {

  private val allowlist: List[Url] = config.allowlist.map(Url.apply).map(sanitize(_))

  private val denylist: List[Url] = config.denylist.map(Url.apply).map(sanitize(_))

  val domainValidator: DomainValidator = DomainValidator.getInstance()

  private val detector: LUrlDetector =
    new LUrlDetector(sanitizeContent(content), LUrlDetectorOptions.valueOf(config.options.value))

  def extract(): List[Url] =
    detector
      .detect()
      .asScala
      .toList
      .map(sanitize(_))
      .filter(checkIfValidDomain)
      .filter(checkAllowlist)
      .filter(checkDenylist)

  private def sanitize(url: String): Url = {
    @tailrec
    def loop(url: String): String =
      if (!endsWithAny(url, ",", "!", "-", ".", "`", "./")) url else loop(url.substring(0, url.length - 1))

    Url(LUrl.create(loop(url)))
  }

  private def sanitizeContent(content: String): String =
    content.replace("https://", " https://").replace("ftp://", " ftp://")

  private def checkIfValidDomain(url: Url): Boolean = {
    def getTld(url: Url): String = {
      val tld = ".".concat(url.toString.split("\\.").last)
      if (tld.contains("/")) tld.split("/").head
      else tld
    }

    if (!Pattern.matches("\\.[0-9]+", getTld(url))) {
      domainValidator.isValidTld(getTld(url))
    } else true
  }

  private def checkAllowlist(url: Url): Boolean =
    allowlist.isEmpty || allowlist.map(_.getHost).contains(url.getHost)

  private def checkDenylist(url: Url): Boolean =
    denylist.isEmpty || !denylist.map(_.getHost).contains(url.getHost)

}
