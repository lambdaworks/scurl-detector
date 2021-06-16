package io.lambdaworks.detection

import com.linkedin.urls.{Url => LUrl}

final case class Url private (underlying: LUrl) extends AnyVal {

  override def toString: String = underlying.getFullUrl

}

object Url {
  def apply(url: String): Url = new Url(LUrl.create(url))
}
