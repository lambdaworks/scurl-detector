import sbt._

object Dependencies {

  private object Versions {
    val Enumeratum = "1.6.1"
    val UrlDetector = "0.1.17"
    val ScalaTest = "3.2.2"
  }

  val enumeratum: ModuleID = "com.beachape" %% "enumeratum" % Versions.Enumeratum
  val urlDetector: ModuleID = "com.linkedin.urls" % "url-detector" % Versions.UrlDetector
  val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.ScalaTest

}
