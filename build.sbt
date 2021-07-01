import Dependencies._

val basicInfo = List(
  name := "ScurlDetector",
  description := "Scala library that detects and extracts URLs from string.",
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
    crossScalaVersions := Seq("2.11.12", "2.12.14", "2.13.6"),
    crossSbtVersions := Vector("0.13.18", "1.5.3"),
    libraryDependencies ++= {
      val core  = List(enumeratum, urlDetector, commonsValidator, scalaCollectionCompat)
      val tests = List(scalaTest).map(_ % Test)

      core ++ tests
    }
  )
