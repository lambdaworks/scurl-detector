package io.lambdaworks.detection

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._

final class UrlDetectorSpec extends AnyFlatSpec with Matchers {

  "URL Detector" should "extract the expected URLs with the default configuration" in {

    val textExpectedUrls =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/hello",
          List(Url("https://lambdaworks.io/hello"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          List(Url("ftp://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          List(Url("http://lambdaworks.io/"))
        ),
        (
          "Parse:wwww.google.com, google.com, slack.test.io!!!!892839283, lw.com/hello,,,, lw.io/something, https://youtube.com/.",
          List(
            Url("wwww.google.com"),
            Url("google.com"),
            Url("slack.test.io"),
            Url("lw.com/hello"),
            Url("lw.io/something"),
            Url("https://youtube.com/")
          )
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq and test.link/aaa 6.30pm as url",
          List(
            Url("http://test.link/g3WMrh"),
            Url("http://test.link/HWRqhq"),
            Url("test.link/aaa")
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
        ),
        (
          "http://013.xxx/",
          List(Url("http://013.xxx/"))
        ),
        (
          "name.lastname@gmail.com",
          Nil
        ),
        (
          "Parse\\u00A0http://test.link/g3WMrh and\\u00A0http://test.link/HWRqhq and test.link/lw",
          List(Url("http://test.link/g3WMrh"), Url("http://test.link/HWRqhq"), Url("http://test.link/lw"))
        ),
        (
          "http://user:pass@host.com host.com",
          List(Url("http://user:pass@host.com"), Url("http://host.com"))
        ),
        (
          "21.10am 21:10pm 12345.com/abc 12345.com/123 21.10am/dmg http://21.10am 9.49am 50.50am 192.168.1.3",
          List(Url("12345.com/abc"), Url("12345.com/123"), Url("192.168.1.3"))
        ),
        (
          """
            |Add <fname>
            |Limited Edition Pre Order available now. All signed by Me!!!
            |Order here - http://store.lw.com....I will be signing some on live soon.Asoon as I get a pen.GO NOW !
            |I hope this.works
            |""".stripMargin,
          List(Url("http://store.lw.com/"), Url("http://this.works/"))
        ),
        (
          """
            |emacsrocks.com are emacs.rocks are valid... valid.me is also valid.
            |mysite.not is not valid
            |world.MINI friend.muTUal lw.offIce for.example
            |""".stripMargin,
          List(
            Url("http://emacsrocks.com/"),
            Url("http://emacs.rocks/"),
            Url("http://valid.me/"),
            Url("http://world.MINI/"),
            Url("http://friend.muTUal/"),
            Url("http://lw.offIce/")
          )
        ),
        (
          "Parse http://www.valid.link. Also, parse this - http://link.me/DM.....",
          List(Url("http://www.valid.link"), Url("http://link.me/DM"))
        ),
        (
          "Hey here's linkhttp://www.google.com arrayhttps://www.google.com https://www.google.com",
          List(Url("http://www.google.com/"), Url("https://www.google.com/"), Url("https://www.google.com/"))
        ),
        (
          "taro@storm.audio janedoe@yahoo.com jdoe@gmail.com",
          Nil
        ),
        (
          "Parse\u00A0http://test.link/g3WMrh and\u00A0http://test.link/HWRqhq and test.link/GaGi",
          List(Url("http://test.link/g3WMrh"), Url("http://test.link/HWRqhq"), Url("test.link/GaGi"))
        )
      )

    val detector = UrlDetector()

    forAll(textExpectedUrls) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default configuration and the specified allowed URLs" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          List(Url("https://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          List(Url("ftp://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          List(Url("http://lambdaworks.io/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Default, Set("http://lambdaworks.io/"), Set.empty))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default configuration and the specified allowed URLs using withAllowed" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          List(Url("https://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          List(Url("ftp://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          List(Url("http://lambdaworks.io/"))
        )
      )

    val detector = UrlDetector(Config.default.withAllowed(Set("http://lambdaworks.io/")))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(
        _.toString
      )
    }

  }

  it should "extract the expected URLs with the default configuration and the specified denied URLs" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          Nil
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Nil
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Nil
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Default, Set.empty, Set("http://lambdaworks.io")))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default configuration and the specified denied URLs using withDenied" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          Nil
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Nil
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Nil
        )
      )

    val detector = UrlDetector(Config.default.withDenied(Set("http://lambdaworks.io")))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(
        _.toString
      )
    }

  }

  it should "extract the expected URLs with quote match enabled and the specified denied URLs" in {

    val testQuoteMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it out \"https://google.com/\"",
          Nil
        ),
        (
          "Hey, this is our website - check it out \"https://lambdaworks.io/\"",
          List(Url("https://lambdaworks.io/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.QuoteMatch, Set.empty, Set("https://google.com/")))

    forAll(testQuoteMatch) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with single quote match enabled" in {

    val testSingleQuoteMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "'https://google.com/'",
          List(Url("https://google.com/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.SingleQuoteMatch, Set.empty, Set.empty))

    forAll(testSingleQuoteMatch) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with single quote match enabled using withOptions" in {

    val testSingleQuoteMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "'https://google.com/'",
          List(Url("https://google.com/"))
        )
      )

    val detector = UrlDetector(Config.default.withOptions(UrlDetectorOptions.SingleQuoteMatch))

    forAll(testSingleQuoteMatch) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with bracket match enabled" in {

    val testBracketMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "(https://google.com/",
          List(Url("https://google.com/"))
        ),
        (
          "[https://google.com/",
          List(Url("https://google.com/"))
        ),
        (
          "Visit {https://google.com/}",
          List(Url("https://google.com/"))
        ),
        (
          "Parse (((http://www.valid.link)(. Also, parse this - ())http://link.me/DM.))(.....",
          List(Url("http://www.valid.link/"), Url("http://link.me/DM"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.BracketMatch, Set.empty, Set.empty))

    forAll(testBracketMatch) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected JSON URLs with the specified allowed URLs" in {

    val testJson =
      Table(
        ("text", "expectedUrls"),
        (
          "{\"site\": \"google.com\"}",
          List(Url("http://google.com/"))
        ),
        (
          "{\"site\": \"lambdaworks.io\"}",
          Nil
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Json, Set("google.com"), Set.empty))

    forAll(testJson) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected Javascript URLs" in {

    val testJavascript =
      Table(
        ("text", "expectedUrls"),
        (
          "var myUrl = \"http://example.com/index.html?param=1&anotherParam=2\"",
          List(Url("http://example.com/index.html?param=1&anotherParam=2"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Javascript, Set.empty, Set.empty))

    forAll(testJavascript) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected HTML URLs with the specified allowed URLs" in {

    val testHtml =
      Table(
        ("text", "expectedUrls"),
        (
          "<img src=\"https://www.google.com/pic\"  width=\"500\" height=\"600\">",
          List(Url("https://www.google.com/pic"))
        ),
        (
          "<img src=\"https://lambdaworks.io/pic\"  width=\"500\" height=\"600\">",
          Nil
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Html, Set("www.google.com"), Set.empty))

    forAll(testHtml) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected XML URLs with the specified denied URLs" in {

    val testXml =
      Table(
        ("text", "expectedUrls"),
        (
          "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">",
          List(Url("http://www.w3.org/1999/XSL/Transform"))
        ),
        (
          "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.google.com\">",
          Nil
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Xml, Set.empty, Set("google.com")))

    forAll(testXml) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with single level domain allowed" in {

    val testAllowSingleLevelDomain =
      Table(
        ("text", "expectedUrls"),
        (
          "Please visit http://localhost",
          List(Url("http://localhost/"))
        ),
        (
          "Please visit go/",
          List(Url("http://go/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.AllowSingleLevelDomain, Set.empty, Set.empty))

    forAll(testAllowSingleLevelDomain) { (text: String, expectedUrls: List[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

}
