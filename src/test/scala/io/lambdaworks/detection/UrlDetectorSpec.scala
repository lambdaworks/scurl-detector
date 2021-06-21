package io.lambdaworks.detection

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._

final class UrlDetectorSpec extends AnyFlatSpec with Matchers {

  val textExpectedUrls =
    Table(
      ("text", "expectedUrls"),
      ("hello this is a url lambdaworks.le", Nil),
      (
        "Parse:wwww.google.com, google.com, slack.test.io!!!!892839283, sprpn.com/hello,,,, sphn.io/something, https://youtube.com/.",
        List(
          Url("wwww.google.com"),
          Url("google.com"),
          Url("slack.test.io"),
          Url("sprpn.com/hello"),
          Url("sphn.io/something"),
          Url("https://youtube.com/")
        )
      ),
      (
        "Parse http://test.link/g3WMrh and http://test.link/HWRqhq and test.link/aaa 6.30pm as url",
        List(
          Url("http://test.link/g3WMrh"),
          Url("http://test.link/HWRqhq"),
          Url("test.link/aaa"),
        )
      ),
      (
        "192.168.1.3 255.255.1.34 1234.34.34.5 0.0.0.0 192.168.1.257 2.3.4.5",
        List(
          Url("192.168.1.3"),
          Url("255.255.1.34"),
          Url("0.0.0.0"),
          Url("2.3.4.5")
        )
      ),
      (
        "Parse http://test.link/g3WMrh and http://test.link/HWRqhq and test.link/aaa",
        List(
          Url("http://test.link/g3WMrh"),
          Url("http://test.link/HWRqhq"),
          Url("test.link/aaa")
        )
      ),
      (
        "pen.GO",
        Nil
      )
    )

  forAll(textExpectedUrls) { (text: String, expectedUrls: List[Url]) =>
    val detector = UrlDetector(text)
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

}
