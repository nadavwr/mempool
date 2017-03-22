import sbt.Keys.publish

lazy val commonSettings = Def.settings(
  scalaVersion := "2.11.8",
  organization := "com.github.nadavwr",
  version := "0.1"
)

lazy val mempool = crossProject(NativePlatform)
  .settings(commonSettings)
  .nativeSettings(
    nativeSharedLibrary := true
  )

lazy val mempoolNative = mempool.native

lazy val sample = crossProject(NativePlatform)
  .dependsOn(mempool)
  .settings(
    commonSettings,
    publish := {},
    publishLocal := {}
  )

lazy val sampleNative = sample.native

lazy val mempoolRoot = (project in file("."))
  .aggregate(mempoolNative, sampleNative)
  .settings(
    commonSettings,
    run := { (run in sampleNative).evaluated },
    publish := {},
    publishLocal := {}
  )

