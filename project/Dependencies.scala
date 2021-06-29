import sbt._

object Dependencies {

  private object Versions {
    val Enumeratum       = "1.6.1"
    val UrlDetector      = "0.1.23"
    val ScalaTest        = "3.2.2"
    val CommonsValidator = "1.7"
  }

  val enumeratum: ModuleID       = "com.beachape"           %% "enumeratum"       % Versions.Enumeratum
  val urlDetector: ModuleID      = "io.github.url-detector" % "url-detector"      % Versions.UrlDetector
  val scalaTest: ModuleID        = "org.scalatest"          %% "scalatest"        % Versions.ScalaTest
  val commonsValidator: ModuleID = "commons-validator"      % "commons-validator" % Versions.CommonsValidator

}
