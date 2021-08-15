import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbtcrossproject.CrossPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import Dependencies._
import Lib._

object Build {

  lazy val root = Project("root", file("."))
    .configure(defaultJvmSettings(tests = false))
    .aggregate(
      baseLawsJS,
      baseLawsJVM,
      baseTestJS,
      baseTestJVM,
      baseTestLawsJS,
      baseTestLawsJVM,
      dbApiJS,
      dbApiJVM,
      dbLawsJS,
      dbLawsJVM,
      dbNodePostgres,
    )

  lazy val baseLawsJVM = baseLaws.jvm
  lazy val baseLawsJS  = baseLaws.js
  lazy val baseLaws = crossProject(JVMPlatform, JSPlatform)
    .configureCross(defaultSettings(NoTests))
    .settings(
      libraryDependencies += Dep.cats.value,
      libraryDependencies += Dep.discipline.value,
    )

  lazy val baseTestJVM = baseTest.jvm
  lazy val baseTestJS  = baseTest.js
  lazy val baseTest = crossProject(JVMPlatform, JSPlatform)
    .configureCross(defaultSettings(NoTests))
    .settings(
      libraryDependencies += Dep.catsEffect.value,
      libraryDependencies += Dep.microlibsTestUtil.value,
    )
    .jsSettings(
      libraryDependencies += Dep.scalaJsReactCallback.value,
      libraryDependencies += Dep.scalaJsReactCallbackCE.value,
    )

  lazy val baseTestLawsJVM = baseTestLaws.jvm
  lazy val baseTestLawsJS  = baseTestLaws.js
  lazy val baseTestLaws = crossProject(JVMPlatform, JSPlatform)
    .dependsOn(baseLaws)
    .configureCross(defaultSettings(NoTests))
    .settings(
      libraryDependencies += Dep.disciplineScalatest.value,
    )

  lazy val dbApiJVM = dbApi.jvm
  lazy val dbApiJS  = dbApi.js
  lazy val dbApi = crossProject(JVMPlatform, JSPlatform)
    .configureCross(defaultSettings(NoTests))
    .settings(
      libraryDependencies += Dep.cats.value,
      libraryDependencies += Dep.catsEffect.value,
    )

  lazy val dbLawsJVM = dbLaws.jvm
  lazy val dbLawsJS  = dbLaws.js
  lazy val dbLaws = crossProject(JVMPlatform, JSPlatform)
    .dependsOn(baseLaws, dbApi)
    .configureCross(defaultSettings(NoTests))

  lazy val dbNodePostgres = project
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(dbApiJS, dbLawsJS % Test, baseTestJS % Test)
    .configure(defaultJsSettings(TestJsWithNode), testsLawsJS)

  // ===================================================================================================================

  def testsLawsJS: Project => Project =
    _.dependsOn(baseTestLawsJS % Test)
}
