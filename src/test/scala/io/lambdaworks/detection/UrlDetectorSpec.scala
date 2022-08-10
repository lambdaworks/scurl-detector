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
          Set(Url("https://lambdaworks.io/hello"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set(Url("ftp://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set(Url("http://lambdaworks.io/"))
        ),
        (
          "Parse:wwww.google.com, google.com, slack.test.io!!!!892839283, lw.com/hello,,,, lw.io/something, https://youtube.com/.",
          Set(
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
          Set(
            Url("http://test.link/g3WMrh"),
            Url("http://test.link/HWRqhq"),
            Url("test.link/aaa")
          )
        ),
        (
          "192.168.1.3 255.255.1.34 1234.34.34.5 0.0.0.0 192.168.1.257 2.3.4.5",
          Set(
            Url("192.168.1.3"),
            Url("255.255.1.34"),
            Url("0.0.0.0"),
            Url("2.3.4.5")
          )
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq and test.link/aaa",
          Set(
            Url("http://test.link/g3WMrh"),
            Url("http://test.link/HWRqhq"),
            Url("test.link/aaa")
          )
        ),
        (
          "pen.GO",
          Set.empty[Url]
        ),
        (
          "http://013.xxx/",
          Set(Url("http://013.xxx/"))
        ),
        (
          "name.lastname@gmail.com",
          Set.empty[Url]
        ),
        (
          "Parse\\u00A0http://test.link/g3WMrh and\\u00A0http://test.link/HWRqhq and test.link/lw",
          Set(Url("http://test.link/g3WMrh"), Url("http://test.link/HWRqhq"), Url("http://test.link/lw"))
        ),
        (
          "http://user:pass@host.com host.com",
          Set(Url("http://user:pass@host.com"), Url("http://host.com"))
        ),
        (
          "21.10am 21:10pm 12345.com/abc 12345.com/123 21.10am/dmg http://21.10am 9.49am 50.50am 192.168.1.3",
          Set(Url("12345.com/abc"), Url("12345.com/123"), Url("192.168.1.3"))
        ),
        (
          """
            |Add <fname>
            |Limited Edition Pre Order available now. All signed by Me!!!
            |Order here - http://store.lw.com....I will be signing some on live soon.Asoon as I get a pen.GO NOW !
            |I hope this.works
            |""".stripMargin,
          Set(Url("http://store.lw.com/"), Url("http://this.works/"))
        ),
        (
          """
            |emacsrocks.com are emacs.rocks are valid... valid.me is also valid.
            |mysite.not is not valid
            |world.MINI friend.muTUal lw.offIce for.example
            |""".stripMargin,
          Set(
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
          Set(Url("http://www.valid.link"), Url("http://link.me/DM"))
        ),
        (
          "Hey here's linkhttp://www.google.com arrayhttps://www.google.com https://www.google.com",
          Set(Url("http://www.google.com/"), Url("https://www.google.com/"), Url("https://www.google.com/"))
        ),
        (
          "taro@storm.audio janedoe@yahoo.com jdoe@gmail.com",
          Set.empty[Url]
        ),
        (
          "Parse\u00A0http://test.link/g3WMrh and\u00A0http://test.link/HWRqhq and test.link/GaGi",
          Set(Url("http://test.link/g3WMrh"), Url("http://test.link/HWRqhq"), Url("test.link/GaGi"))
        )
      )

    val detector = UrlDetector()

    forAll(textExpectedUrls) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default configuration and the specified allowed URLs" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          Set(Url("https://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set(Url("ftp://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set(Url("http://lambdaworks.io/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Default, Set(Url("http://lambdaworks.io/")), Set.empty))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default configuration and the specified allowed URLs using withAllowed" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          Set(Url("https://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set(Url("ftp://lambdaworks.io/"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set(Url("http://lambdaworks.io/"))
        )
      )

    val detector = UrlDetector(Config.default.withAllowed(Set(Url("http://lambdaworks.io/"))))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: Set[Url]) =>
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
          Set.empty[Url]
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set.empty[Url]
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Default, Set.empty, Set(Url("http://lambdaworks.io"))))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default configuration and the specified denied URLs using withDenied" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          Set.empty[Url]
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set.empty[Url]
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(Config.default.withDenied(Set(Url("http://lambdaworks.io"))))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: Set[Url]) =>
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
          Set.empty[Url]
        ),
        (
          "Hey, this is our website - check it out \"https://lambdaworks.io/\"",
          Set(Url("https://lambdaworks.io/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.QuoteMatch, Set.empty, Set(Url("https://google.com/"))))

    forAll(testQuoteMatch) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with single quote match enabled" in {

    val testSingleQuoteMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "'https://google.com/'",
          Set(Url("https://google.com/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.SingleQuoteMatch, Set.empty, Set.empty))

    forAll(testSingleQuoteMatch) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with single quote match enabled using withOptions" in {

    val testSingleQuoteMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "'https://google.com/'",
          Set(Url("https://google.com/"))
        )
      )

    val detector = UrlDetector(Config.default.withOptions(UrlDetectorOptions.SingleQuoteMatch))

    forAll(testSingleQuoteMatch) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with bracket match enabled" in {

    val testBracketMatch =
      Table(
        ("text", "expectedUrls"),
        (
          "(https://google.com/",
          Set(Url("https://google.com/"))
        ),
        (
          "[https://google.com/",
          Set(Url("https://google.com/"))
        ),
        (
          "Visit {https://google.com/}",
          Set(Url("https://google.com/"))
        ),
        (
          "Parse (((http://www.valid.link)(. Also, parse this - ())http://link.me/DM.))(.....",
          Set(Url("http://www.valid.link/"), Url("http://link.me/DM"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.BracketMatch, Set.empty, Set.empty))

    forAll(testBracketMatch) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected JSON URLs with the specified allowed URLs" in {

    val testJson =
      Table(
        ("text", "expectedUrls"),
        (
          "{\"site\": \"google.com\"}",
          Set(Url("http://google.com/"))
        ),
        (
          "{\"site\": \"lambdaworks.io\"}",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Json, Set(Url("google.com")), Set.empty))

    forAll(testJson) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected Javascript URLs" in {

    val testJavascript =
      Table(
        ("text", "expectedUrls"),
        (
          "var myUrl = \"http://example.com/index.html?param=1&anotherParam=2\"",
          Set(Url("http://example.com/index.html?param=1&anotherParam=2"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Javascript, Set.empty, Set.empty))

    forAll(testJavascript) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected HTML URLs with the specified allowed URLs" in {

    val testHtml =
      Table(
        ("text", "expectedUrls"),
        (
          "<img src=\"https://www.google.com/pic\"  width=\"500\" height=\"600\">",
          Set(Url("https://www.google.com/pic"))
        ),
        (
          "<img src=\"https://lambdaworks.io/pic\"  width=\"500\" height=\"600\">",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Html, Set(Url("www.google.com")), Set.empty))

    forAll(testHtml) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected XML URLs with the specified denied URLs" in {

    val testXml =
      Table(
        ("text", "expectedUrls"),
        (
          "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">",
          Set(Url("http://www.w3.org/1999/XSL/Transform"))
        ),
        (
          "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.google.com\">",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.Xml, Set.empty, Set(Url("google.com"))))

    forAll(testXml) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with single level domain allowed" in {

    val testAllowSingleLevelDomain =
      Table(
        ("text", "expectedUrls"),
        (
          "Please visit http://localhost",
          Set(Url("http://localhost/"))
        ),
        (
          "Please visit go/",
          Set(Url("http://go/"))
        )
      )

    val detector = UrlDetector(Config(UrlDetectorOptions.AllowSingleLevelDomain, Set.empty, Set.empty))

    forAll(testAllowSingleLevelDomain) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

}
