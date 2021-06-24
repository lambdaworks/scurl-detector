package io.lambdaworks.detection

import com.linkedin.urls.{Url => LUrl}

final case class Url private (underlying: LUrl) extends AnyVal {

  override def toString: String = underlying.getFullUrl

  def getHost: String = underlying.getHost

  def contained(urls: List[Url]): Boolean = urls.map(_.getHost).contains(getHost)

}

object Url {
  def apply(url: String): Url = new Url(LUrl.create(url))

  implicit def url2String(url: Url): String = url.toString
}
