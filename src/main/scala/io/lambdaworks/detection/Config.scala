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
)
