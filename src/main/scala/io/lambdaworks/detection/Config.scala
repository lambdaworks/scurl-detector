package io.lambdaworks.detection

/**
 * Represents configuration of URL detector.
 *
 *  @param options detector options
 *  @param allowed set of allowed URLs
 *  @param denied set of denied URLs
 */
final case class Config(
  options: UrlDetectorOptions,
  allowed: Set[Url],
  denied: Set[Url]
) {

  /**
   * Method that creates a [[io.lambdaworks.detection.Config]] with detector options.
   *
   * @param options detector option (see [[io.lambdaworks.detection.UrlDetectorOptions]])
   * @return new [[io.lambdaworks.detection.Config]] with applied options
   */
  def withOptions(options: UrlDetectorOptions): Config =
    this.copy(options = options)

  /**
   * Method that creates a [[io.lambdaworks.detection.Config]] with set of allowed URLs.
   *
   * @param urls set of URLs to allow
   * @return new [[io.lambdaworks.detection.Config]] with applied set of allowed URLs
   */
  def withAllowed(urls: Set[Url]): Config =
    this.copy(allowed = urls)

  /**
   * Method that creates a [[io.lambdaworks.detection.Config]] with set of denied URLs.
   *
   * @param urls set of URLs to deny
   * @return new [[io.lambdaworks.detection.Config]] with applied set of denied URLs
   */
  def withDenied(urls: Set[Url]): Config =
    this.copy(denied = urls)

}

object Config {

  lazy val default: Config = Config(UrlDetectorOptions.Default, Set.empty, Set.empty)

}
