import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import Dependencies._
import Lib._

object Build {

  lazy val root = Project("root", file("."))
    .configure(defaultJvmSettings(tests = false))
    .aggregate(
      baseLaws,
      baseTest,
      baseTestLaws,
      dbApi,
      dbLaws,
      dbNodePostgres,
    )

  lazy val baseLaws = project
    .enablePlugins(ScalaJSPlugin)
    .configure(defaultJsSettings(NoTests))
    .settings(
      libraryDependencies ++= Seq(
        Dep.cats      .value,
        Dep.discipline.value,
      ),
    )

  lazy val baseTest = project
    .enablePlugins(ScalaJSPlugin)
    .configure(defaultJsSettings(NoTests))
    .settings(
      libraryDependencies ++= Seq(
        Dep.microlibsTestUtil.value,
        Dep.scalaJsReactCallback.value,
      ),
    )

  lazy val baseTestLaws = project
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(baseLaws)
    .configure(defaultJsSettings(NoTests))
    .settings(
      libraryDependencies += Dep.disciplineScalatest.value,
    )

  lazy val dbApi = project
    .enablePlugins(ScalaJSPlugin)
    .configure(defaultJsSettings(NoTests))

  lazy val dbLaws = project
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(baseLaws, dbApi)
    .configure(defaultJsSettings(NoTests))

  lazy val dbNodePostgres = project
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(dbApi, dbLaws % Test, baseTest % Test)
    .configure(defaultJsSettings(TestJsWithNode), testsLaws)

  // ===================================================================================================================

  def testsLaws: Project => Project =
    _.dependsOn(baseTestLaws % Test)
}
