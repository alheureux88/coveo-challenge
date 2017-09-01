scalaVersion := Common.scalaV

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.coveo.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.coveo.binders._"


lazy val root = (project in file("."))
  .aggregate(utils, web)
  .settings(
    run := {
      (run in web in Compile).evaluated
    }
  )

lazy val web = project
  .configs(IntegrationTest)
  .settings(Common.commonSettings: _*)
  .settings( Defaults.itSettings : _*)
  .settings(libraryDependencies ++= Seq(
    "io.swagger" %% "swagger-play2" % "1.6.0",
    "me.xdrop" % "fuzzywuzzy" % "1.1.8",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % "it,test",
    "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
    ws
  ),
    sourceDirectory in IntegrationTest := baseDirectory.value / "it"
  )
  .enablePlugins(GitVersioning, LauncherJarPlugin, JavaAppPackaging, PlayScala)
  .dependsOn(utils)

//Project to share class between every services
lazy val utils = project
  .settings(Common.commonSettings: _*)