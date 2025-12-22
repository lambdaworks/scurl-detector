package io.lambdaworks.detection

import io.lemonlabs.uri.{Host, Url}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._

final class UrlDetectorSpec extends AnyFlatSpec with Matchers {

  "URL Detector" should "extract the expected URLs with the default options" in {

    val textExpectedUrls =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/hello",
          Set(Url.parse("https://lambdaworks.io/hello"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set(Url.parse("ftp://lambdaworks.io"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set(Url.parse("http://lambdaworks.io"))
        ),
        (
          "Parse:wwww.google.com, google.com, slack.test.io!!!!892839283, lw.com/hello,,,, lw.io/something, https://youtube.com/.",
          Set(
            Url.parse("http://wwww.google.com"),
            Url.parse("http://google.com"),
            Url.parse("http://slack.test.io"),
            Url.parse("http://lw.com/hello"),
            Url.parse("http://lw.io/something"),
            Url.parse("https://youtube.com")
          )
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq and test.link/aaa 6.30pm as url",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/aaa")
          )
        ),
        (
          "Parse https://learn.microsoft.com/en-us/previous-versions/windows/internet-explorer/ie-developer/platform-apis/aa752574(v=vs.85)?redirectedfrom=MSDN",
          Set(
            Url.parse(
              "https://learn.microsoft.com/en-us/previous-versions/windows/internet-explorer/ie-developer/platform-apis/aa752574(v=vs.85)?redirectedfrom=MSDN"
            )
          )
        ),
        (
          "192.168.1.3 255.255.1.34 1234.34.34.5 0.0.0.0 192.168.1.257 2.3.4.5",
          Set(
            Url.parse("http://192.168.1.3"),
            Url.parse("http://255.255.1.34"),
            Url.parse("http://0.0.0.0"),
            Url.parse("http://2.3.4.5")
          )
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq and test.link/aaa",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/aaa")
          )
        ),
        (
          "pen.GO",
          Set.empty[Url]
        ),
        (
          "http://013.xxx/",
          Set(Url.parse("http://013.xxx"))
        ),
        (
          "name.lastname@gmail.com",
          Set.empty[Url]
        ),
        (
          "Parse\\u00A0http://test.link/g3WMrh and\\u00A0http://test.link/HWRqhq and test.link/lw",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/lw")
          )
        ),
        (
          "http://user:pass@host.com host.com",
          Set(Url.parse("http://user:pass@host.com"), Url.parse("http://host.com"))
        ),
        (
          "21.10am 21:10pm 12345.com/abc 12345.com/123 21.10am/dmg http://21.10am 9.49am 50.50am 192.168.1.3",
          Set(Url.parse("http://12345.com/abc"), Url.parse("http://12345.com/123"), Url.parse("http://192.168.1.3"))
        ),
        (
          """
            |Add <fname>
            |Limited Edition Pre Order available now. All signed by Me!!!
            |Order here - http://store.lw.com....I will be signing some on live soon.Asoon as I get a pen.GO NOW !
            |I hope this.works
            |""".stripMargin,
          Set(Url.parse("http://store.lw.com"), Url.parse("http://this.works"))
        ),
        (
          """
            |emacsrocks.com are emacs.rocks are valid... valid.me is also valid.
            |mysite.not is not valid
            |world.MINI friend.muTUal lw.offIce for.example
            |""".stripMargin,
          Set(
            Url.parse("http://emacsrocks.com"),
            Url.parse("http://emacs.rocks"),
            Url.parse("http://valid.me"),
            Url.parse("http://world.MINI"),
            Url.parse("http://friend.muTUal"),
            Url.parse("http://lw.offIce")
          )
        ),
        (
          "Parse http://www.valid.link. Also, parse this - http://link.me/DM.....",
          Set(Url.parse("http://www.valid.link"), Url.parse("http://link.me/DM"))
        ),
        (
          "Hey here's linkhttp://www.google.com arrayhttps://www.google.com https://www.google.com",
          Set(
            Url.parse("http://www.google.com"),
            Url.parse("https://www.google.com"),
            Url.parse("https://www.google.com")
          )
        ),
        (
          "taro@storm.audio janedoe@yahoo.com jdoe@gmail.com",
          Set.empty[Url]
        ),
        (
          "Parse\u00A0http://test.link/g3WMrh and\u00A0http://test.link/HWRqhq and test.link/GaGi",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/GaGi")
          )
        )
      )

    val detector = UrlDetector.default

    forAll(textExpectedUrls) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default options and the specified allowed URLs" in {

    val textExpectedUrlsAllowedDenied =
      Table(
        ("text", "expectedUrls"),
        (
          "Hey, this is our website - check it outhttps://lambdaworks.io/",
          Set(Url.parse("https://lambdaworks.io"))
        ),
        (
          "Hey, this is our website - check it outftp://lambdaworks.io./",
          Set(Url.parse("ftp://lambdaworks.io"))
        ),
        (
          "Hey, this is our website - check it outhttp://lambdaworks.io./",
          Set(Url.parse("http://lambdaworks.io"))
        )
      )

    val detector = UrlDetector.default.withAllowed(Host.parse("lambdaworks.io"))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract the expected URLs with the default options and the specified denied URLs" in {

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

    val detector = UrlDetector.default.withDenied(Host.parse("lambdaworks.io"))

    forAll(textExpectedUrlsAllowedDenied) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
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
          Set(Url.parse("https://lambdaworks.io"))
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.QuoteMatch).withDenied(Host.parse("google.com"))

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
          Set(Url.parse("https://google.com"))
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.SingleQuoteMatch)

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
          Set(Url.parse("https://google.com"))
        ),
        (
          "[https://google.com/",
          Set(Url.parse("https://google.com"))
        ),
        (
          "Visit {https://google.com/}",
          Set(Url.parse("https://google.com"))
        ),
        (
          "Parse (((http://www.valid.link)(. Also, parse this - ())http://link.me/DM.))(.....",
          Set(Url.parse("http://www.valid.link"), Url.parse("http://link.me/DM"))
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq().pdf and (test.link/KeKy)",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/KeKy")
          )
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq{}.pdf and {test.link/KeKy}",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/KeKy")
          )
        ),
        (
          "Parse http://test.link/g3WMrh and http://test.link/HWRqhq{}.pdf and {test.link/KeKy}",
          Set(
            Url.parse("http://test.link/g3WMrh"),
            Url.parse("http://test.link/HWRqhq"),
            Url.parse("http://test.link/KeKy")
          )
        ),
        (
          "Parse https://site.com/(v=1.2)",
          Set(Url.parse("https://site.com/(v=1.2"))
        ),
        (
          "Parse https://learn.microsoft.com/en-us/previous-versions/windows/internet-explorer/ie-developer/platform-apis/aa752574(v=vs.85)?redirectedfrom=MSDN " +
            "and http://www.website.com/?utm_source=google%5BB%2B%5D&utm_medium=cpc&utm_content=google_ad(B)&utm_campaign=product",
          Set(
            Url.parse(
              "https://learn.microsoft.com/en-us/previous-versions/windows/internet-explorer/ie-developer/platform-apis/aa752574(v=vs.85)?redirectedfrom=MSDN"
            ),
            Url.parse(
              "http://www.website.com/?utm_source=google%5BB%2B%5D&utm_medium=cpc&utm_content=google_ad(B)&utm_campaign=product"
            )
          )
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.BracketMatch)

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
          Set(Url.parse("http://google.com"))
        ),
        (
          "{\"site\": \"lambdaworks.io\"}",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.Json).withAllowed(Host.parse("google.com"))

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
          Set(Url.parse("http://example.com/index.html?param=1&anotherParam=2"))
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.Javascript)

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
          Set(Url.parse("https://www.google.com/pic"))
        ),
        (
          "<img src=\"https://lambdaworks.io/pic\"  width=\"500\" height=\"600\">",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.Html).withAllowed(Host.parse("www.google.com"))

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
          Set(Url.parse("http://www.w3.org/1999/XSL/Transform"))
        ),
        (
          "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.google.com\">",
          Set.empty[Url]
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.Xml).withDenied(Host.parse("google.com"))

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
          Set(Url.parse("http://localhost"))
        ),
        (
          "Please visit go/",
          Set(Url.parse("http://go"))
        )
      )

    val detector = UrlDetector(UrlDetectorOptions.AllowSingleLevelDomain)

    forAll(testAllowSingleLevelDomain) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract URLs with leading or trailing encoded spaces correctly" in {

    val testEncodedSpaces =
      Table(
        ("text", "expectedUrls"),
        (
          "Check out http://%20leadingspace.com for more info",
          Set(Url.parse("http://leadingspace.com"))
        ),
        (
          "Visit http://trailingspace.com%20 today",
          Set(Url.parse("http://trailingspace.com"))
        ),
        (
          "Both http://%20bothspaces.com%20 here",
          Set(Url.parse("http://bothspaces.com"))
        ),
        (
          "Multiple http://%20%20%20multispace.com spaces",
          Set(Url.parse("http://multispace.com"))
        ),
        (
          "Normal https://normalurl.com/path should work",
          Set(Url.parse("https://normalurl.com/path"))
        ),
        (
          "Path http://example.com/%20path with space encoding",
          Set(Url.parse("http://example.com/%20path"))
        )
      )

    val detector = UrlDetector.default

    forAll(testEncodedSpaces) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

  it should "extract protocol-relative URLs" in {

    val testProtocolRelativeUrls =
      Table(
        ("text", "expectedUrls"),
        (
          "Check out //lambdaworks.io for more info",
          Set(Url.parse("https://lambdaworks.io"))
        ),
        (
          "Visit //www.example.com/path/to/resource",
          Set(Url.parse("https://www.example.com/path/to/resource"))
        ),
        (
          "Multiple URLs: //google.com and //github.com/repo",
          Set(Url.parse("https://google.com"), Url.parse("https://github.com/repo"))
        )
      )

    val detector = UrlDetector.default

    forAll(testProtocolRelativeUrls) { (text: String, expectedUrls: Set[Url]) =>
      detector.extract(text).map(_.toString) shouldBe expectedUrls.map(_.toString)
    }

  }

}
