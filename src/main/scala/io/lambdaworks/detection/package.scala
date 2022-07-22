package io.lambdaworks

import com.linkedin.urls.{Url => LUrl}

import scala.language.implicitConversions

package object detection {

  implicit def lurl2String(url: LUrl): String = url.toString

}
