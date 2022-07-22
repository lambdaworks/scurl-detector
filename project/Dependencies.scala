import sbt._

object Dependencies {

  private object Versions {
    val Enumeratum            = "1.7.0"
    val UrlDetector           = "0.1.23"
    val ScalaTest             = "3.2.12"
    val CommonsValidator      = "1.7"
    val ScalaCollectionCompat = "2.8.0"
    val OrganizeImports       = "0.5.0"
    val Scaluzzi              = "0.1.20"
  }

  lazy val All =
    List(
      "com.beachape"           %% "enumeratum"              % Versions.Enumeratum,
      "io.github.url-detector"  % "url-detector"            % Versions.UrlDetector,
      "org.scalatest"          %% "scalatest"               % Versions.ScalaTest % Test,
      "commons-validator"       % "commons-validator"       % Versions.CommonsValidator,
      "org.scala-lang.modules" %% "scala-collection-compat" % Versions.ScalaCollectionCompat
    )

  lazy val ScalaFix =
    List(
      "com.github.liancheng" %% "organize-imports" % Versions.OrganizeImports,
      "com.github.vovapolu"  %% "scaluzzi"         % Versions.Scaluzzi
    )

}
