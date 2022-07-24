package io.lambdaworks.detection

import com.linkedin.urls.detection.{UrlDetector => LUrlDetector, UrlDetectorOptions => LUrlDetectorOptions}
import com.linkedin.urls.{Url => LUrl}
import org.apache.commons.lang3.StringUtils.endsWithAny
import org.apache.commons.validator.routines.{DomainValidator, EmailValidator}

import java.util.regex.Pattern
import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

/**
 * Represents URL detector.
 *
 *  @param content text from which URLs are being extracted
 *  @param config URL detector configuration
 */
final case class UrlDetector(content: String, config: Config = Config()) {

  private val allowlist: List[Url] = config.allowlist.map(Url.apply).map(sanitize(_))

  private val denylist: List[Url] = config.denylist.map(Url.apply).map(sanitize(_))

  private val domainValidator: DomainValidator = DomainValidator.getInstance()

  private val emailValidator: EmailValidator = EmailValidator.getInstance()

  private val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(config.options.value))

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with detector options.
   *
   * @param options detector option (see [[io.lambdaworks.detection.UrlDetectorOptions]])
   * @return new [[io.lambdaworks.detection.UrlDetector]] with applied config
   */
  def withOptions(options: UrlDetectorOptions): UrlDetector =
    this.copy(config = config.copy(options = options))

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with list of allowed URLs.
   *
   * @param urls list of URLs to allow
   * @return new [[io.lambdaworks.detection.UrlDetector]] with applied config
   */
  def withAllowlist(urls: List[String]): UrlDetector =
    this.copy(config = config.copy(allowlist = urls))

  /**
   * Method that creates a [[io.lambdaworks.detection.UrlDetector]] with list of denied URLs.
   *
   * @param urls list of URLs to deny
   * @return new [[io.lambdaworks.detection.UrlDetector]] with applied config
   */
  def withDenylist(urls: List[String]): UrlDetector =
    this.copy(config = config.copy(denylist = urls))

  /**
   * Method that extracts URLs from text.
   *
   *  @return list of found URLs
   */
  def extract(): List[Url] = {
    def isEmail(url: Url): Boolean =
      emailValidator.isValid(url.toString.replaceAll("http://|https://|ftp://", "").dropRight(1))

    def checkIfValidDomain(url: Url): Boolean = {
      def getTld(url: Url): String =
        ".".concat(url.getHost.split("\\.").last)

      if (!Pattern.matches("\\.[0-9]+", getTld(url))) {
        domainValidator.isValidTld(getTld(url))
      } else true
    }

    detector
      .detect()
      .asScala
      .toList
      .map(sanitize(_))
      .filterNot(isEmail)
      .filter(u => config.options == UrlDetectorOptions.AllowSingleLevelDomain || checkIfValidDomain(u))
      .filter(allowlist.isEmpty || _.contained(allowlist))
      .filterNot(_.contained(denylist))
  }

  private def sanitize(url: String): Url = {
    @tailrec
    def loop(url: String): String =
      if (!endsWithAny(url, ",", "!", "-", ".", "`", "./")) url else loop(url.substring(0, url.length - 1))

    Url(LUrl.create(loop(url)))
  }


}