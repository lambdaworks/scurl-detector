package io.lambdaworks.detection

/**
 * Represents configuration of URL detector.
 *
 *  @param options detector options
 *  @param allowlist list of allowed URLs
 *  @param denylist list of forbidden URLs
 */
final case class Config(
  options: UrlDetectorOptions = UrlDetectorOptions.Default,
  allowlist: List[String] = Nil,
  denylist: List[String] = Nil
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
   * Method that creates a [[io.lambdaworks.detection.Config]] with list of allowed URLs.
   *
   * @param urls list of URLs to allow
   * @return new [[io.lambdaworks.detection.Config]] with applied list of allowed URLs
   */
  def withAllowlist(urls: List[String]): Config =
    this.copy(allowlist = urls)

  /**
   * Method that creates a [[io.lambdaworks.detection.Config]] with list of denied URLs.
   *
   * @param urls list of URLs to deny
   * @return new [[io.lambdaworks.detection.Config]] with applied list of denied URLs
   */
  def withDenylist(urls: List[String]): Config =
    this.copy(denylist = urls)

}

object Config {

  lazy val default: Config = Config()

}
