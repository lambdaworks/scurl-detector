package io.lambdaworks.detection

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.linkedin.urls.{Url => LUrl}

final class UrlDetectorSpec extends AnyFlatSpec with Matchers {

  "extract" should "return list of URLs" in {
    val detector = UrlDetector("hello this is a url Linkedin.com")
    detector.extract().map(_.toString) shouldBe List(Url(LUrl.create("http://Linkedin.com/")).toString)
  }

}
