package io.lambdaworks.detection

import enumeratum.values._

sealed abstract class UrlDetectorOptions(val value: String) extends StringEnumEntry

object UrlDetectorOptions extends StringEnum[UrlDetectorOptions] {

  val values = findValues

  case object Default                extends UrlDetectorOptions(value = "Default")
  case object QuoteMatch             extends UrlDetectorOptions(value = "QUOTE_MATCH")
  case object SingleQuoteMatch       extends UrlDetectorOptions(value = "SINGLE_QUOTE_MATCH")
  case object BracketMatch           extends UrlDetectorOptions(value = "BRACKET_MATCH")
  case object Json                   extends UrlDetectorOptions(value = "JSON")
  case object Javascript             extends UrlDetectorOptions(value = "JAVASCRIPT")
  case object Xml                    extends UrlDetectorOptions(value = "XML")
  case object Html                   extends UrlDetectorOptions(value = "HTML")
  case object AllowSingleLevelDomain extends UrlDetectorOptions(value = "ALLOW_SINGLE_LEVEL_DOMAIN")
}
