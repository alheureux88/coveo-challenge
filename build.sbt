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
  .settings(Common.commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "io.swagger" %% "swagger-play2" % "1.6.0",
    "me.xdrop" % "fuzzywuzzy" % "1.1.8",
    ws
  ))
  .enablePlugins(GitVersioning, LauncherJarPlugin, JavaAppPackaging, PlayScala)
  .dependsOn(utils)

//Project to share class between every services
lazy val utils = project
  .settings(Common.commonSettings: _*)