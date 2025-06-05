package io.lambdaworks.detection

import cats.data.NonEmptySet
import cats.data.NonEmptySet._
import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import io.lemonlabs.uri.Host.orderHost
import io.lemonlabs.uri._
import org.apache.commons.validator.routines.EmailValidator

import scala.collection.immutable.SortedSet
import scala.jdk.CollectionConverters._
import scala.util.matching.Regex

import UrlDetector._

final class UrlDetector private (
  options: UrlDetectorOptions,
  allowed: Option[NonEmptySet[Host]],
  denied: Option[NonEmptySet[Host]],
  emailValidator: EmailValidator
) {

  private val allowedWithoutWww: Option[NonEmptySet[Host]] =
    allowed.flatMap(allowed => NonEmptySet.fromSet(allowed.toSortedSet.flatMap(removeWwwSubdomain)))

  private val deniedWithoutWww: Option[NonEmptySet[Host]] =
    denied.flatMap(denied => NonEmptySet.fromSet(denied.toSortedSet.flatMap(removeWwwSubdomain)))

  /**
   * Method that extracts URLs from text.
   *
   * @param content text from which URLs are being extracted
   * @return set of found URLs
   */

  def extract(content: String): Set[AbsoluteUrl] = {
    val detector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(options.value))

    detector
      .detect()
      .asScala
      .toList
      .map(url => AbsoluteUrl.parse(sanitize(cleanUrlForBracketMatch(content, url.toString))))
      .filter(url => allowedUrl(url) && notEmail(url) && validTopLevelDomain(url))
      .toSet
  }

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with a set of hosts to allow.
   *
   * @param host required host to allow
   * @param hosts additional hosts to allow
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the applied set of hosts
   */
  def withAllowed(host: Host, hosts: Host*): UrlDetector =
    new UrlDetector(
      options,
      Some(NonEmptySet(host, SortedSet(hosts: _*))),
      denied,
      emailValidator
    )

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with a set of hosts to deny.
   *
   * @param host required host to deny
   * @param hosts additional hosts to deny
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the applied set of hosts
   */
  def withDenied(host: Host, hosts: Host*): UrlDetector =
    new UrlDetector(
      options,
      allowed,
      Some(NonEmptySet(host, SortedSet(hosts: _*))),
      emailValidator
    )

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with URL detector options.
   *
   * @param options URL detector options (see [[io.lambdaworks.detection.UrlDetectorOptions]])
   * @return new [[io.lambdaworks.detection.UrlDetector]] with applied options
   */
  def withOptions(options: UrlDetectorOptions): UrlDetector =
    new UrlDetector(options, allowed, denied, emailValidator)

  private def allowedUrl(url: AbsoluteUrl): Boolean =
    allowedWithoutWww.forall(containsHost(_, url)) && deniedWithoutWww.forall(!containsHost(_, url))

  private def cleanUrlForBracketMatch(content: String, url: String): String = {
    def isAllowedUrlChar(c: Char): Boolean =
      c.isLetterOrDigit || AllowedSpecialChars.contains(c)

    Option(content.indexOf(url)).filter(_ >= 0).fold(url) { from =>
      val extendedUrl = content.substring(from).takeWhile(isAllowedUrlChar)

      EmptyParensRegex
        .findFirstMatchIn(extendedUrl)
        .fold(extendedUrl)(m => extendedUrl.substring(0, m.start))
    }
  }

  private def containsHost(hosts: NonEmptySet[Host], url: AbsoluteUrl): Boolean =
    hosts.exists(host => host.subdomain.fold(host.apexDomain.exists(url.apexDomain.contains))(_ => host == url.host))

  private def isIp(url: AbsoluteUrl): Boolean = url.host match {
    case _ @(_: IpV4 | _: IpV6) => true
    case _                      => false
  }

  private def notEmail(url: AbsoluteUrl): Boolean =
    !emailValidator.isValid(url.toProtocolRelativeUrl.toString.replace("//", ""))

  private def removeWwwSubdomain(host: Host): Option[Host] =
    if (host.subdomain.contains("www")) {
      host.apexDomain.flatMap(Host.parseOption)
    } else {
      Option(host)
    }

  private def sanitize(url: String): String =
    SanitizeRegex.replaceFirstIn(url, "")

  private def validSuffix(url: AbsoluteUrl): Boolean =
    url.host.normalize.publicSuffix.isDefined

  private def validTopLevelDomain(url: AbsoluteUrl): Boolean =
    options == UrlDetectorOptions.AllowSingleLevelDomain || validSuffix(url) || isIp(url)

}

object UrlDetector {

  /**
   * Creates a [[io.lambdaworks.detection.UrlDetector]] with the specified [[io.lambdaworks.detection.UrlDetectorOptions]]
   *
   * @param options URL detector options
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the specified URL detector options
   */
  def apply(options: UrlDetectorOptions): UrlDetector =
    new UrlDetector(options, None, None, EmailValidator.getInstance())

  /**
   * A [[io.lambdaworks.detection.UrlDetector]] with the default options
   */
  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default)

  private final val AllowedSpecialChars: Set[Char] = Set(
    '-', '.', '_', '~', ':', '/', '?', '#', '[', ']', '@', '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', '%'
  )

  private final val EmptyParensRegex: Regex = "\\(\\)[^()]*".r
  private final val SanitizeRegex: Regex    = "[,!-.`/]+$".r

  implicit private[detection] val orderingHost: Ordering[Host] = orderHost.toOrdering

}
