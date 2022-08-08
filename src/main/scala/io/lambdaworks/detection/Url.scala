package io.lambdaworks.detection

import com.linkedin.urls.{Url => LUrl}

import scala.language.implicitConversions

/**
 * Represents a URL along with utility methods.
 *
 *  @param underlying [[com.linkedin.urls.Url]]
 */
final case class Url private (underlying: LUrl) extends AnyVal {

  override def toString: String = underlying.getFullUrl

  /**
   * Gets host part of URL by invoking underlying's method getHost
   * @return string value of host
   */
  def host: String = underlying.getHost

  /**
   * Checks if list of URLs contains certain URL. We consider that www.url.com and url.com are same URL.
   *
   * @param urls list of URLs
   * @return boolean if this URL is contained in the provided list of URLs
   */
  def containedIn(urls: List[Url]): Boolean = {
    val replacedUrl = host.replace("www.", "")
    urls.map(_.host.replace("www.", "")).contains(replacedUrl)
  }

}

object Url {

  /**
   * Creates Url object from string value of URL
   *
   *  @param url string value of URL
   *  @return new [[io.lambdaworks.detection.Url]] instance
   */
  def apply(url: String): Url = new Url(LUrl.create(url))

  implicit def url2String(url: Url): String = url.toString
}
