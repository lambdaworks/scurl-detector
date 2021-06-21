package io.lambdaworks.detection

import java.util.regex.Pattern

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import com.linkedin.urls.{Url => LUrl}
import org.apache.commons.lang3.StringUtils.endsWithAny
import org.apache.commons.validator.routines.DomainValidator

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

final case class UrlDetector(content: String, options: UrlDetectorOptions = UrlDetectorOptions.Default) {

  val domainValidator: DomainValidator = DomainValidator.getInstance()

  private val detector: LUrlDetector =
    new LUrlDetector(sanitizeContent(content), LUrlDetectorOptions.valueOf(options.value))

  def extract(): List[Url] =
    detector.detect().asScala.toList.map(sanitize(_)).filter(checkIfValidDomain)

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
}
