import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala212: String = "2.12.18"
val Scala213: String = "2.13.13"
val Scala3: String   = "3.3.1"

inThisBuild(
  List(
    scalaVersion         := Scala213,
    organization         := "io.lambdaworks",
    organizationName     := "LambdaWorks",
    organizationHomepage := Some(url("https://www.lambdaworks.io/")),
    homepage             := Some(url("https://lambdaworks.github.io/scurl-detector/")),
    description          := "Scala library that detects and extracts URLs from text.",
    licenses             := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "lambdaworks",
        "LambdaWorks' Team",
        "admin@lambdaworks.io",
        url("https://github.com/lambdaworks")
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
    name               := "scurl-detector",
    crossScalaVersions := List(Scala212, Scala213, Scala3),
    libraryDependencies ++= All,
    buildInfoKeys     := List[BuildInfoKey](organization, name, version),
    buildInfoPackage  := "detection",
    semanticdbEnabled := scalaVersion.value != Scala3, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => "-Wunused"
        case _             => "-Ywarn-unused-import"
      }
    }
  )
  .enablePlugins(BuildInfoPlugin)

lazy val docs = (project in file("scurl-detector-docs"))
  .settings(
    mdocVariables := Map(
      "SNAPSHOT_VERSION" -> version.value
    ),
    moduleName                                 := "scurl-detector-docs",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(root),
    ScalaUnidoc / unidoc / target              := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite     := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
  .dependsOn(root)
