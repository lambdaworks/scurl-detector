package io.lambdaworks.detection

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import org.apache.commons.lang3.StringUtils.endsWithAny
import org.apache.commons.validator.routines.{DomainValidator, EmailValidator}

import scala.annotation.tailrec
import scala.jdk.CollectionConverters._
import scala.util.matching.Regex

import UrlDetector._

/**
 * Represents URL detector.
 *
 * @param config URL detector configuration
 */
final class UrlDetector(config: Config) {

  private val allowed: Set[Url] = config.allowed.map(url => sanitize(url))

  private val denied: Set[Url] = config.denied.map(url => sanitize(url))

  private val domainValidator: DomainValidator = DomainValidator.getInstance()

  private val emailValidator: EmailValidator = EmailValidator.getInstance()

  /**
   * Method that extracts URLs from text.
   *
   * @param content text from which URLs are being extracted
   * @return set of found URLs
   */
  def extract(content: String): Set[Url] = {
    val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(config.options.value))

    detector
      .detect()
      .asScala
      .toList
      .map(lurl => sanitize(Url(lurl)))
      .filter(url =>
        (allowed.isEmpty || url.containedIn(allowed))
          && !url.containedIn(denied)
          && (config.options == UrlDetectorOptions.AllowSingleLevelDomain || checkIfValidDomain(url))
          && !isEmail(url)
      )
      .toSet
  }

  private def sanitize(url: Url): Url = {
    @tailrec
    def loop(url: String): String =
      if (!endsWithAny(url, ",", "!", "-", ".", "`", "./")) url else loop(url.substring(0, url.length - 1))

    Url(loop(url.toString))
  }

  private def isEmail(url: Url): Boolean =
    emailValidator.isValid(schemeRegex.replaceAllIn(url.toString, "").dropRight(1))

  def checkIfValidDomain(url: Url): Boolean = {
    val topLevelDomain = "." + dotRegex.split(url.host).last

    numberTopLevelDomainRegex.matches(topLevelDomain) || domainValidator.isValidTld(topLevelDomain)
  }

}

object UrlDetector {

  def apply(): UrlDetector = new UrlDetector(Config.default)

  def apply(config: Config): UrlDetector = new UrlDetector(config)

  private val schemeRegex: Regex = "http://|https://|ftp://".r

  private val dotRegex: Regex = "\\.".r

  private val numberTopLevelDomainRegex: Regex = "\\.[0-9]+".r

}
