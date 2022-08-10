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
 * @param config URL detector configuration
 */
class UrlDetector(config: Config) {

  private val allowed: Set[Url] = config.allowed.map(sanitize(_))

  private val denied: Set[Url] = config.denied.map(sanitize(_))

  private val domainValidator: DomainValidator = DomainValidator.getInstance()

  private val emailValidator: EmailValidator = EmailValidator.getInstance()

  /**
   * Method that extracts URLs from text.
   *
   * @param content text from which URLs are being extracted
   * @return list of found URLs
   */
  def extract(content: String): List[Url] = {
    def isEmail(url: Url): Boolean =
      emailValidator.isValid(url.toString.replaceAll("http://|https://|ftp://", "").dropRight(1))

    def checkIfValidDomain(url: Url): Boolean = {
      def getTopLevelDomain(url: Url): String =
        ".".concat(url.host.split("\\.").last)

      Pattern.matches("\\.[0-9]+", getTopLevelDomain(url)) || domainValidator.isValidTld(getTopLevelDomain(url))
    }

    val detector: LUrlDetector = new LUrlDetector(content, LUrlDetectorOptions.valueOf(config.options.value))

    detector
      .detect()
      .asScala
      .toList
      .map(sanitize(_))
      .filterNot(isEmail)
      .filter(u => config.options == UrlDetectorOptions.AllowSingleLevelDomain || checkIfValidDomain(u))
      .filter(allowed.isEmpty || _.containedIn(allowed))
      .filterNot(_.containedIn(denied))
  }

  private def sanitize(url: String): Url = {
    @tailrec
    def loop(url: String): String =
      if (!endsWithAny(url, ",", "!", "-", ".", "`", "./")) url else loop(url.substring(0, url.length - 1))

    Url(LUrl.create(loop(url)))
  }

}

object UrlDetector {

  def apply(): UrlDetector = new UrlDetector(Config.default)

  def apply(config: Config): UrlDetector = new UrlDetector(config)

}
