import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    scalaVersion         := "2.13.8",
    organization         := "io.lambdaworks",
    organizationName     := "LambdaWorks",
    organizationHomepage := Some(url("https://www.lambdaworks.io/")),
    homepage             := Some(url("https://lambdaworks.github.io/scurl-detector/")),
    description          := "Scala library that detects and extracts URLs from text.",
    licenses             := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
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
    crossScalaVersions := Seq("2.12.16", "2.13.8"),
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

lazy val docs = (project in file("scurl-detector-docs"))
  .settings(
    moduleName                                 := "scurl-detector-docs",
    mdocIn                                     := (LocalRootProject / baseDirectory).value / "docs",
    mdocOut                                    := (LocalRootProject / baseDirectory).value / "website" / "docs",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(root),
    ScalaUnidoc / unidoc / target              := (LocalRootProject / baseDirectory).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite     := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
  .dependsOn(root)
