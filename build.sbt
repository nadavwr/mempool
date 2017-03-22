import sbt.Keys.publish

lazy val commonSettings = Def.settings(
  scalaVersion := "2.11.8",
  organization := "com.github.nadavwr",
  version := "0.3.0-SNAPSHOT"
)

lazy val mempool = project
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings)
  .settings(
    nativeSharedLibrary := true
  )

lazy val sample = project
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(mempool)
  .settings(
    commonSettings,
    publish := {},
    publishLocal := {}
  )

lazy val mempoolRoot = (project in file("."))
  .aggregate(mempool, sample)
  .settings(
    commonSettings,
    run := { (run in sample).evaluated },
    publish := {},
    publishLocal := {}
  )

