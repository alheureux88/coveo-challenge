import sbt.Keys._
import sbt._
import play.sbt.PlayImport._

object Common {
  val scalaV: String = "2.12.2"
  val releaseVersion = "0.0.1-SNAPSHOT"

  name := """coveo"""
  version := "1.0-SNAPSHOT"

  val commonSettings: Seq[Setting[_]] = Seq(
    organization := "com.coveo",
    scalaVersion := scalaV,
    scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8"),
    libraryDependencies ++= playDeps
  )

  val playDeps = Seq(
    guice,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test,
    "nl.grons" %% "metrics-scala" % "3.5.9_a2.4"
  )
}