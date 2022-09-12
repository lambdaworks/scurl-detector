import sbt._

object Dependencies {

  private object Versions {

    val UrlDetector           = "0.1.23"
    val ScalaTest             = "3.2.13"
    val CommonsValidator      = "1.7"
    val ScalaCollectionCompat = "2.8.1"
    val ScalaUri              = "4.0.2"
    val OrganizeImports       = "0.5.0"
    val Scaluzzi              = "0.1.23"

  }

  import Versions._

  lazy val All: List[ModuleID] =
    List(
      "io.github.url-detector"  % "url-detector"            % UrlDetector,
      "commons-validator"       % "commons-validator"       % CommonsValidator,
      "org.scala-lang.modules" %% "scala-collection-compat" % ScalaCollectionCompat,
      "io.lemonlabs"           %% "scala-uri"               % ScalaUri,
      "org.scalatest"          %% "scalatest"               % ScalaTest % Test
    )

  lazy val ScalaFix: List[ModuleID] =
    List(
      "com.github.liancheng" %% "organize-imports" % OrganizeImports,
      "com.github.vovapolu"  %% "scaluzzi"         % Scaluzzi
    )

}
