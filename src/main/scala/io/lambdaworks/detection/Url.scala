package io.lambdaworks.detection

import com.linkedin.urls.{Url => LUrl}

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
   * Checks if set of URLs contains certain URL. We consider that www.url.com and url.com are the same URL.
   *
   * @param urls set of URLs
   * @return boolean if this URL is contained in the provided set of URLs
   */
  def containedIn(urls: Set[Url]): Boolean = {
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

}
