import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

val basicInfo = List(
  name        := "ScurlDetector",
  description := "Scala library that detects and extracts URLs from string.",
  version     := "0.0.1-rc.1"
)

val organizationInfo = List(
  organization         := "io.lambdaworks",
  organizationName     := "LambdaWorks",
  organizationHomepage := Some(new URL("https://www.lambdaworks.io/"))
)

addCommandAlias("prepare", "fix; fmt")
addCommandAlias("check", "fixCheck; fmtCheck")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("fix", "scalafixAll")
addCommandAlias("fixCheck", "scalafixAll --check")

ThisBuild / scalafixDependencies ++= ScalaFix
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)

val root = (project in file("."))
  .settings(basicInfo: _*)
  .settings(organizationInfo: _*)
  .settings(
    scalaVersion       := "2.13.8",
    crossScalaVersions := Seq("2.12.15", "2.13.8"),
    libraryDependencies ++= All,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => "-Wunused"
        case _             => "-Ywarn-unused-import"
      }
    }
  )
