package io.lambdaworks.detection

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import io.lemonlabs.uri._
import org.apache.commons.validator.routines.EmailValidator

import scala.jdk.CollectionConverters._
import scala.util.matching.Regex

import UrlDetector._

/**
 * Represents URL detector.
 *
 * @param config URL detector configuration
 */
final class UrlDetector private (
  config: Config,
  allowed: Set[Host],
  denied: Set[Host],
  emailValidator: EmailValidator
) {

  /**
   * Method that extracts URLs from text.
   *
   * @param content text from which URLs are being extracted
   * @return set of found URLs
   */
  def extract(content: String): Set[AbsoluteUrl] = {
    val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(config.options.value))

    detector
      .detect()
      .asScala
      .toList
      .map(lUrl => AbsoluteUrl.parse(sanitize(lUrl.toString)))
      .filter(url =>
        (allowed.isEmpty || hostToApexDomainHost(url.host).exists(allowed.contains))
          && !hostToApexDomainHost(url.host).exists(denied.contains)
          && (config.options == UrlDetectorOptions.AllowSingleLevelDomain || checkIfValidDomain(url))
          && !isEmail(url)
      )
      .toSet
  }

  private def isEmail(url: AbsoluteUrl): Boolean =
    emailValidator.isValid(url.toProtocolRelativeUrl.toString.replace("//", ""))

}

object UrlDetector {

  def apply(config: Config): UrlDetector = new UrlDetector(
    config,
    config.allowed.flatMap(hostToApexDomainHost),
    config.denied.flatMap(hostToApexDomainHost),
    EmailValidator.getInstance()
  )

  lazy val default: UrlDetector = UrlDetector(Config.default)

  private val SanitizeRegex: Regex = "[,!-.`/]+$".r

  private def sanitize(url: String): String = SanitizeRegex.replaceFirstIn(url, "")

  private def hostToApexDomainHost(host: Host): Option[Host] = host match {
    case host: DomainName => host.apexDomain.map(Host.parse)
    case host             => Option(host)
  }

  private def checkIfValidDomain(url: AbsoluteUrl): Boolean =
    url.host.normalize.publicSuffix.isDefined || url.hostOption.exists {
      case _ @(_: IpV4 | _: IpV6) => true
      case _                      => false
    }

}
