import Dependencies._

val basicInfo = List(
  name := "ScurlDetector",
  description := "Provide better description. :)",
  version := "0.0.1-rc.1"
)

val organizationInfo = List(
  organization := "io.lambdaworks",
  organizationName := "LambdaWorks",
  organizationHomepage := Some(new URL("https://www.lambdaworks.io/"))
)

val root = (project in file("."))
  .settings(basicInfo: _*)
  .settings(organizationInfo: _*)
  .settings(
    scalaVersion := "2.13.6",
    libraryDependencies ++= {
      val core = List(enumeratum, urlDetector)
      val tests = List(scalaTest).map(_ % Test)

      core ++ tests
    }
  )
