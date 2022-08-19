import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    name                 := "scurl-detector",
    description          := "Scala library that detects and extracts URLs from text.",
    organization         := "io.lambdaworks",
    organizationName     := "LambdaWorks",
    organizationHomepage := Some(url("https://www.lambdaworks.io/")),
    homepage             := Some(url("https://lambdaworks.github.io/scurl-detector/")),
    licenses             := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "mvelimir",
        "Velimir Milinković",
        "velimir@lambdaworks.io",
        url("https://github.com/mvelimir")
      ),
      Developer(
        "drmarjanovic",
        "Dragutin Marjanović",
        "dragutin@lambdaworks.io",
        url("https://github.com/drmarjanovic")
      )
    )
  )
)

// Sonatype publish options
sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"

addCommandAlias("prepare", "fix; fmt")
addCommandAlias("check", "fixCheck; fmtCheck")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheckAll")
addCommandAlias("fix", "scalafixAll")
addCommandAlias("fixCheck", "scalafixAll --check")

ThisBuild / scalafixDependencies ++= ScalaFix
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)

lazy val root = (project in file("."))
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
