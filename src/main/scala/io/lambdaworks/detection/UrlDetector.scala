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
 * @param options URL detector configuration
 * @param allowed set of allowed hosts
 * @param denied set of denied hosts
 */
final class UrlDetector private (
  options: UrlDetectorOptions,
  allowed: Set[Host],
  denied: Set[Host],
  emailValidator: EmailValidator
) {

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with URL detector options.
   *
   * @param options URL detector options (see [[io.lambdaworks.detection.UrlDetectorOptions]])
   * @return new [[io.lambdaworks.detection.UrlDetector]] with applied options
   */
  def withOptions(options: UrlDetectorOptions): UrlDetector = UrlDetector(options, allowed, denied)

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with a set of hosts to allow.
   *
   * @param allowed set of hosts to allow
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the applied set of hosts
   */
  def withAllowed(allowed: Set[Host]): UrlDetector = UrlDetector(options, allowed, denied)

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with a set of hosts to deny.
   *
   * @param denied set of hosts to deny
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the applied set of hosts
   */
  def withDenied(denied: Set[Host]): UrlDetector = UrlDetector(options, allowed, denied)

  /**
   * Method that extracts URLs from text.
   *
   * @param content text from which URLs are being extracted
   * @return set of found URLs
   */
  def extract(content: String): Set[AbsoluteUrl] = {
    val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(options.value))

    detector
      .detect()
      .asScala
      .toList
      .map(lUrl => AbsoluteUrl.parse(sanitize(lUrl.toString)))
      .filter(url =>
        (allowed.isEmpty || removeWwwSubdomain(url.host).exists(allowed.contains))
          && !removeWwwSubdomain(url.host).exists(denied.contains)
          && (options == UrlDetectorOptions.AllowSingleLevelDomain || checkIfValidDomain(url))
          && !isEmail(url)
      )
      .toSet
  }

  private def sanitize(url: String): String = SanitizeRegex.replaceFirstIn(url, "")

  private def checkIfValidDomain(url: AbsoluteUrl): Boolean =
    url.host.normalize.publicSuffix.isDefined || url.hostOption.exists {
      case _ @(_: IpV4 | _: IpV6) => true
      case _                      => false
    }

  private def isEmail(url: AbsoluteUrl): Boolean =
    emailValidator.isValid(url.toProtocolRelativeUrl.toString.replace("//", ""))

}

object UrlDetector {

  def apply(options: UrlDetectorOptions, allowed: Set[Host], denied: Set[Host]): UrlDetector = new UrlDetector(
    options,
    allowed.flatMap(removeWwwSubdomain),
    denied.flatMap(removeWwwSubdomain),
    EmailValidator.getInstance()
  )

  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default, Set.empty, Set.empty)

  private final val SanitizeRegex: Regex = "[,!-.`/]+$".r

  private def removeWwwSubdomain(host: Host): Option[Host] = if (host.subdomain.contains("www")) {
    host.apexDomain.map(Host.parse)
  } else {
    Option(host)
  }

}
