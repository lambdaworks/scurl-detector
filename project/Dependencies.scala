import sbt.*

object Dependencies {

  private object Versions {

    val UrlDetector           = "0.1.23"
    val ScalaTest             = "3.2.19"
    val CommonsValidator      = "1.10.0"
    val ScalaCollectionCompat = "2.13.0"
    val ScalaUri              = "4.2.0"
    val OrganizeImports       = "0.6.0"
    val Scaluzzi              = "0.1.23"

  }

  import Versions.*

  lazy val All: List[ModuleID] =
    List(
      "io.github.url-detector"  % "url-detector"            % UrlDetector,
      "commons-validator"       % "commons-validator"       % CommonsValidator,
      "org.scala-lang.modules" %% "scala-collection-compat" % ScalaCollectionCompat,
      "com.indoorvivants"      %% "scala-uri"               % ScalaUri,
      "org.scalatest"          %% "scalatest"               % ScalaTest % Test
    )

  lazy val ScalaFix: List[ModuleID] =
    List(
      "com.github.liancheng" %% "organize-imports" % OrganizeImports,
      "com.github.vovapolu"  %% "scaluzzi"         % Scaluzzi
    )

}
