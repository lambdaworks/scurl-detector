package io.lambdaworks.detection

sealed abstract class UrlDetectorOptions(val value: String)

/** The options to use when detecting URLs. */
object UrlDetectorOptions {

  /** Default options, no special checks. */
  case object Default extends UrlDetectorOptions(value = "Default")

  /**
   * Matches quotes in the beginning and end of string.
   * If a string starts with a quote, then the ending quote will be eliminated. For example,
   * "https://lambdaworks.io" will pull out just 'https://lambdaworks.io' instead of 'https://lambdaworks.io"'
   */
  case object QuoteMatch extends UrlDetectorOptions(value = "QUOTE_MATCH")

  /**
   * Matches single quotes in the beginning and end of a string. For example,
   * "'https://lambdaworks.io'" will pull out just "https://lambdaworks.io" instead of "https://lambdaworks.io'"
   */
  case object SingleQuoteMatch extends UrlDetectorOptions(value = "SINGLE_QUOTE_MATCH")

  /**
   * Matches brackets and closes on the second one.
   * Same as quote matching but works for brackets such as (), {}, []. For example,
   * "(https://lambdaworks.io)" will pull out just "https://lambdaworks.io" instead of "https://lambdaworks.io)"
   */
  case object BracketMatch extends UrlDetectorOptions(value = "BRACKET_MATCH")

  /** Checks for bracket characters and more importantly quotes to start and end strings. */
  case object Json extends UrlDetectorOptions(value = "JSON")

  /** Checks JSON format or but also looks for a single quote. */
  case object Javascript extends UrlDetectorOptions(value = "JAVASCRIPT")

  /**
   * Checks for XML characters and uses them as ending characters as well as quotes.
   * This also includes quote_matching.
   */
  case object Xml extends UrlDetectorOptions(value = "XML")

  /** Checks all of the rules besides brackets. This is XML but also can contain Javascript. */
  case object Html extends UrlDetectorOptions(value = "HTML")

  /** Checks for single level domains as well. Ex: go/, http://localhost */
  case object AllowSingleLevelDomain extends UrlDetectorOptions(value = "ALLOW_SINGLE_LEVEL_DOMAIN")

}
