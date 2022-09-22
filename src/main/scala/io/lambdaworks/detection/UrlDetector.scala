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
  allowedOption: Option[NonEmptySet[Host]],
  denied: Set[Host],
  emailValidator: EmailValidator
) {

  private val allowedWithoutWwwOption: Option[Set[Host]] =
    allowedOption.map(_.toSortedSet.flatMap(removeWwwSubdomain))

  private val deniedWithoutWww: Set[Host] = denied.flatMap(removeWwwSubdomain)

  private def removeWwwSubdomain(host: Host): Option[Host] =
    if (host.subdomain.contains("www")) {
      host.apexDomain.flatMap(Host.parseOption)
    } else {
      Option(host)
    }

  private def sanitize(url: String): String =
    SanitizeRegex.replaceFirstIn(url, "")

  private def containsHost(hosts: Set[Host], url: AbsoluteUrl): Boolean =
    hosts.exists(host => host.subdomain.fold(host.apexDomain.exists(url.apexDomain.contains))(_ => host == url.host))

  private def allowedUrl(url: AbsoluteUrl): Boolean =
    allowedWithoutWwwOption.forall(containsHost(_, url)) && !containsHost(deniedWithoutWww, url)

  private def notEmail(url: AbsoluteUrl): Boolean =
    !emailValidator.isValid(url.toProtocolRelativeUrl.toString.replace("//", ""))

  private def validSuffix(url: AbsoluteUrl): Boolean =
    url.host.normalize.publicSuffix.isDefined

  private def isIp(url: AbsoluteUrl): Boolean = url.host match {
    case _ @(_: IpV4 | _: IpV6) => true
    case _                      => false
  }

  private def validTopLevelDomain(url: AbsoluteUrl): Boolean =
    options == UrlDetectorOptions.AllowSingleLevelDomain || validSuffix(url) || isIp(url)

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with URL detector options.
   *
   * @param options URL detector options (see [[io.lambdaworks.detection.UrlDetectorOptions]])
   * @return new [[io.lambdaworks.detection.UrlDetector]] with applied options
   */
  def withOptions(options: UrlDetectorOptions): UrlDetector =
    new UrlDetector(options, allowedOption, denied, emailValidator)

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with a set of hosts to allow.
   *
   * @param allowed set of hosts to allow
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the applied set of hosts
   */
  def withAllowed(allowed: NonEmptySet[Host]): UrlDetector =
    new UrlDetector(options, Option(allowed), denied, emailValidator)

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
      Option(NonEmptySet(host, SortedSet(hosts: _*))),
      denied,
      emailValidator
    )

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with a set of hosts to deny.
   *
   * @param denied set of hosts to deny
   * @return new [[io.lambdaworks.detection.UrlDetector]] with the applied set of hosts
   */
  def withDenied(denied: NonEmptySet[Host]): UrlDetector =
    new UrlDetector(options, allowedOption, denied.toSortedSet, emailValidator)

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
      allowedOption,
      Set(host +: hosts: _*),
      emailValidator
    )

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
      .map(lUrl => AbsoluteUrl.parse(sanitize(lUrl.toString)))
      .filter(url => allowedUrl(url) && notEmail(url) && validTopLevelDomain(url))
      .toSet
  }

}

object UrlDetector {

  def apply(options: UrlDetectorOptions): UrlDetector =
    new UrlDetector(options, None, Set.empty[Host], EmailValidator.getInstance())

  lazy val default: UrlDetector = UrlDetector(UrlDetectorOptions.Default)

  private final val SanitizeRegex: Regex = "[,!-.`/]+$".r

  implicit val orderingHost: Ordering[Host] = orderHost.toOrdering

}
