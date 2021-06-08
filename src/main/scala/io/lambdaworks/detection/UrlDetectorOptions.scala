package io.lambdaworks.detection

import enumeratum.values._

sealed abstract class UrlDetectorOptions(val name: String, val value: Int) extends IntEnumEntry

object UrlDetectorOptions extends IntEnum[UrlDetectorOptions] {

  val values = findValues

  case object Default extends UrlDetectorOptions(name = "Default", value = 0)
  case object QuoteMatch extends UrlDetectorOptions(name = "QUOTE_MATCH", value = 1)
  case object SingleQuoteMatch extends UrlDetectorOptions(name = "SINGLE_QUOTE_MATCH", value = 2)
  case object BracketMatch extends UrlDetectorOptions(name = "BRACKET_MATCH", value = 3)
  case object Json extends UrlDetectorOptions(name = "JSON", value = 4)
  case object Javasript extends UrlDetectorOptions(name = "JAVASCRIPT", value = 5)
  case object Xml extends UrlDetectorOptions(name = "XML", value = 6)
  case object Html extends UrlDetectorOptions(name = "HTML", value = 7)
  case object AllowSingleLevelDomain extends UrlDetectorOptions(name = "ALLOW_SINGLE_LEVEL_DOMAIN", value = 8)
}
