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
      dbApi,
      dbLaws,
      dbNodePostgres,
    )

  lazy val dbApi = project
    .enablePlugins(ScalaJSPlugin)
    .configure(defaultJsSettings(NoTests))

  lazy val dbLaws = project
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(dbApi)
    .configure(defaultJsSettings(NoTests))
    .settings(
      libraryDependencies ++= Seq(
        Dep.cats      .value,
        Dep.discipline.value,
      ),
    )

  lazy val dbNodePostgres = project
    .enablePlugins(ScalaJSPlugin)
    .dependsOn(dbApi, dbLaws % "compile->test")
    .configure(defaultJsSettings(TestJsWithNode))
    .settings(
      libraryDependencies ++= Seq(
        Dep.disciplineScalatest.value % Test,
      ),
    )
}
