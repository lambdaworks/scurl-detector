package io.lambdaworks.detection

import io.lemonlabs.uri.Host

/**
 * Represents configuration of URL detector.
 *
 *  @param options detector options
 *  @param allowed set of allowed hosts
 *  @param denied set of denied hosts
 */
final case class Config(
  options: UrlDetectorOptions,
  allowed: Set[Host],
  denied: Set[Host]
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
   * Method that creates a [[io.lambdaworks.detection.Config]] with set of allowed hosts.
   *
   * @param urls set of URLs to allow
   * @return new [[io.lambdaworks.detection.Config]] with applied set of allowed hosts
   */
  def withAllowed(hosts: Set[Host]): Config =
    this.copy(allowed = hosts)

  /**
   * Method that creates a [[io.lambdaworks.detection.Config]] with set of denied hosts.
   *
   * @param urls set of URLs to deny
   * @return new [[io.lambdaworks.detection.Config]] with applied set of denied hosts
   */
  def withDenied(hosts: Set[Host]): Config =
    this.copy(denied = hosts)

}

object Config {

  lazy val default: Config = Config(UrlDetectorOptions.Default, Set.empty, Set.empty)

}
