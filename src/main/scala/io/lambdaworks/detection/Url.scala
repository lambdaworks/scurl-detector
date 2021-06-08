package io.lambdaworks.detection

import com.linkedin.urls.{Url => LUrl}

final case class Url private (underlying: LUrl) extends AnyVal
