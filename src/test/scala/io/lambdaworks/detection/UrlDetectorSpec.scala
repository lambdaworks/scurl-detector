package io.lambdaworks.detection

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._

final class UrlDetectorSpec extends AnyFlatSpec with Matchers {

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
        "http://www.google.com/blah/..",
        List(Url("http://www.google.com/blah/"))
      ),
      (
        "http://3279880203/blah",
        List(Url("http://3279880203/blah"))
      ),
      (
        "http://host.com/ab%23cd",
        List(Url("http://host.com/ab%23cd"))
      ),
      (
        "http://lw.com/foo#bar#baz",
        List(Url("http://lw.com/foo#bar#baz"))
      ),
      (
        "http://go.co/a/b/../c",
        List(Url("http://go.co/a/b/../c"))
      ),
      (
        "http://195.127.0.11/blah",
        List(Url("http://195.127.0.11/blah"))
      ),
      (
        "http://big.big.boss@176.32.103.205/",
        List(Url("http://big.big.boss@176.32.103.205/"))
      ),
      (
        "http://www.gotaport.com:1234/",
        List(Url("http://www.gotaport.com:1234/"))
      ),
      (
        "%20leadingspace.com",
        List(Url("http://%20leadingspace.com/"))
      ),
      (
        "http://168.188.99.26/.secure/www.ebay.com/",
        List(Url("http://168.188.99.26/.secure/www.ebay.com/"))
      ),
      (
        "bewp.bop.com/boop?bip=2&bep=3",
        List(Url("bewp.bop.com/boop?bip=2&bep=3"))
      ),
      (
        "http://touch.www.lw.com:9000",
        List(Url("http://touch.www.lw.com:9000"))
      ),
      (
        "http://bah.com/lala/@1234/@dfd@df?@dsf#ono",
        List(Url("http://bah.com/lala/@1234/@dfd@df?@dsf#ono"))
      ),
      (
        "https://dewd:dood@www.google.com:20/?why=is&this=test#?@Sdsf",
        List(Url("https://dewd:dood@www.google.com:20/?why=is&this=test#?@Sdsf"))
      ),
      (
        "http://ono:a@fboo.com:90/dhdh/@1234",
        List(Url("http://ono:a@fboo.com:90/dhdh/@1234"))
      ),
      (
        "ftp://whosdere:me@google.com/",
        List(Url("ftp://whosdere:me@google.com/"))
      ),
      (
        "ono:doope@fb.net:9090/dhdh",
        List(Url("http://ono:doope@fb.net:9090/dhdh"))
      ),
      (
        "https://www.google.com/search?q=aaa",
        List(Url("https://www.google.com/search?q=aaa"))
      ),
      (
        "lalal:@www.gogo.com http://fbeoo@boop.com/dhdeh/@1234?aj=r",
        List(Url("http://fbeoo@boop.com/dhdeh/@1234?aj=r"))
      ),
      (
        "http://\\\\x01\\\\x80.com/",
        List(Url("http://x80.com/"))
      ),
      (
        "http://www.google.com/foo\\tbar\\rbaz\\n2",
        List(Url("http://www.google.com/foo"))
      )
    )

  forAll(textExpectedUrls) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text)
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

  val textExpectedUrlsAllowDenyList =
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

  forAll(textExpectedUrlsAllowDenyList) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.Default, List("http://lambdaworks.io/")))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

  val textExpectedUrlsAllowDenyList2 =
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

  forAll(textExpectedUrlsAllowDenyList2) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.Default, Nil, List("http://lambdaworks.io")))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

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

  forAll(testQuoteMatch) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.QuoteMatch, Nil, List("https://google.com/")))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

  val testSingleQuoteMatch =
    Table(
      ("text", "expectedUrls"),
      (
        "'https://google.com/'",
        List(Url("https://google.com/"))
      )
    )

  forAll(testSingleQuoteMatch) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.SingleQuoteMatch))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

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

  forAll(testBracketMatch) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.BracketMatch))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

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

  forAll(testJson) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.Json, List("google.com")))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

  val testJavascript =
    Table(
      ("text", "expectedUrls"),
      (
        "var myUrl = \"http://example.com/index.html?param=1&anotherParam=2\"",
        List(Url("http://example.com/index.html?param=1&anotherParam=2"))
      )
    )

  forAll(testJavascript) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.Javascript))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

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

  forAll(testHtml) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.Html, List("www.google.com")))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

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

  forAll(testXml) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.Xml, Nil, List("google.com")))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

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
      ),
      (
        "http://host/%25%32%35%25%32%35",
        List(Url("http://host/%25%32%35%25%32%35"))
      ),
      (
        "http://www.0xb02067cd/",
        List(Url("http://www.0xb02067cd/"))
      )
    )

  forAll(testAllowSingleLevelDomain) { (text: String, expectedUrls: List[Url]) =>
    val detector =
      UrlDetector(text, Config(UrlDetectorOptions.AllowSingleLevelDomain))
    detector.extract().map(_.toString) shouldBe expectedUrls.map(_.toString)
  }

}
