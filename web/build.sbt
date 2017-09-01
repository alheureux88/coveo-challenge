git.useGitDescribe := true
git.baseVersion := "0.0.0"

val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r
git.gitTagToVersionNumber := {
  case VersionRegex(v,"") => Some(v)
  case VersionRegex(v,"SNAPSHOT") => Some(s"$v-SNAPSHOT")
  case VersionRegex(v,s) => Some(s"$v-$s-SNAPSHOT")
  case _ => None
}

import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}
import sbtrelease._
// we hide the existing definition for setReleaseVersion to replace it with our own
import sbtrelease.ReleaseStateTransformations.{setReleaseVersion=> _,_}

def setVersionOnly(selectVersion: Versions => String): ReleaseStep = { st: State =>
  val vs = st.get(ReleaseKeys.versions).getOrElse(sys.error("No versions are set! Was this release part executed before inquireVersions?"))
  val selected = selectVersion(vs)

  st.log.info("Setting version to '%s'." format selected)
  val useGlobal =Project.extract(st).get(releaseUseGlobalVersion)
  val versionStr = (if (useGlobal) globalVersionString else versionString) format selected

  reapply(Seq(
    if (useGlobal) {version in ThisBuild := selected}
    else {version := selected}
  ), st)
}

lazy val setReleaseVersion: ReleaseStep = setVersionOnly(_._1)

releaseVersion <<= releaseVersionBump( bumper=>{
  ver => Version(ver)
    .map(_.withoutQualifier)
    .map(_.bump(bumper).string).getOrElse(versionFormatError)
})

releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  runTest,
  tagRelease,
  publishArtifacts,
  ReleaseStep(releaseStepTask(publishLocal in Docker)),
  pushChanges
)

dockerCommands := Seq(
  Cmd("FROM", "java:latest"),
  Cmd("WORKDIR", "/opt/docker"),
  Cmd("ADD", "opt", "/opt"),
  ExecCmd("RUN", "chmod", "-R", "+x", "."),
  ExecCmd("RUN", "chown", "-R", "daemon:daemon", "."),
  Cmd("USER", "daemon"),
  Cmd("EXPOSE", "9000"),
  Cmd("ENTRYPOINT", "bin/web")
)

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)

javaOptions in Docker ++= Seq(
  "-Dpidfile.path=/dev/null"
)

dockerUpdateLatest := true
dockerUsername := Some("kushanagi")
packageName in Docker := "coveo"