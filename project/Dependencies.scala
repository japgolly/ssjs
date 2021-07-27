import sbt._
import sbt.librarymanagement.ModuleFilter
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object Ver {
    def betterMonadicFor       = "0.3.1"
    def cats                   = "2.6.1"
    def catsEffect             = "3.2.0"
 // def circe                  = "0.14.1"
 // def clearConfig            = "1.4.0"
    def discipline             = "1.1.5"
    def disciplineScalatest    = "2.1.5" // TODO: I should make a discipline-utest
    def kindProjector          = "0.13.0"
    def microlibs              = "3.0"
 // def nyaya                  = "0.10.0"
    def scala                  = "2.13.6"
 // def scalaJsDom             = "1.1.0"
    def scalaJsReact           = "2.0.0-RC2"
 // def univEq                 = "1.4.0"
    def utest                  = "0.7.10"
 // def webappUtil             = "1.1.0"
  }

  object Dep {
    val cats                        = Def.setting("org.typelevel"                      %% "cats-core"                % Ver.cats)
    val catsEffect                  = Def.setting("org.typelevel"                      %% "cats-effect"              % Ver.catsEffect)
 // val circe                       = Def.setting("io.circe"                          %%% "circe-core"               % Ver.circe)
 // val circeParser                 = Def.setting("io.circe"                          %%% "circe-parser"             % Ver.circe)
 // val clearConfig                 = Def.setting("com.github.japgolly.clearconfig"   %%% "core"                     % Ver.clearConfig)
    val discipline                  = Def.setting("org.typelevel"                     %%% "discipline-core"          % Ver.discipline)
    val disciplineScalatest         = Def.setting("org.typelevel"                     %%% "discipline-scalatest"     % Ver.disciplineScalatest)
 // val microlibsAdtMacros          = Def.setting("com.github.japgolly.microlibs"     %%% "adt-macros"               % Ver.microlibs)
 // val microlibsMacroUtils         = Def.setting("com.github.japgolly.microlibs"     %%% "macro-utils"              % Ver.microlibs)
 // val microlibsNonempty           = Def.setting("com.github.japgolly.microlibs"     %%% "nonempty"                 % Ver.microlibs)
 // val microlibsRecursion          = Def.setting("com.github.japgolly.microlibs"     %%% "recursion"                % Ver.microlibs)
 // val microlibsScalazExt          = Def.setting("com.github.japgolly.microlibs"     %%% "scalaz-ext"               % Ver.microlibs)
 // val microlibsStdlibExt          = Def.setting("com.github.japgolly.microlibs"     %%% "stdlib-ext"               % Ver.microlibs)
    val microlibsTestUtil           = Def.setting("com.github.japgolly.microlibs"     %%% "test-util"                % Ver.microlibs)
 // val microlibsUtils              = Def.setting("com.github.japgolly.microlibs"     %%% "utils"                    % Ver.microlibs)
 // val nyayaGen                    = Def.setting("com.github.japgolly.nyaya"         %%% "nyaya-gen"                % Ver.nyaya)
 // val nyayaProp                   = Def.setting("com.github.japgolly.nyaya"         %%% "nyaya-prop"               % Ver.nyaya)
 // val nyayaTest                   = Def.setting("com.github.japgolly.nyaya"         %%% "nyaya-test"               % Ver.nyaya)
 // val scalaJsDom                  = Def.setting("org.scala-js"                      %%% "scalajs-dom"              % Ver.scalaJsDom)
    val scalaJsReactCallback        = Def.setting("com.github.japgolly.scalajs-react" %%% "callback"                 % Ver.scalaJsReact)
    val scalaJsReactCallbackCE      = Def.setting("com.github.japgolly.scalajs-react" %%% "callback-ext-cats_effect" % Ver.scalaJsReact)
 // val scalaJsReactCore            = Def.setting("com.github.japgolly.scalajs-react" %%% "core"                     % Ver.scalaJsReact)
 // val scalaJsReactExtra           = Def.setting("com.github.japgolly.scalajs-react" %%% "extra"                    % Ver.scalaJsReact)
 // val scalaJsReactTest            = Def.setting("com.github.japgolly.scalajs-react" %%% "test"                     % Ver.scalaJsReact)
 // val univEq                      = Def.setting("com.github.japgolly.univeq"        %%% "univeq"                   % Ver.univEq)
    val utest                       = Def.setting("com.lihaoyi"                       %%% "utest"                    % Ver.utest)
 // val webappUtilProtocol          = Def.setting("com.github.japgolly.webapp-util"   %%% "protocol"                 % Ver.webappUtil)
 // val webappUtilProtocolCirce     = Def.setting("com.github.japgolly.webapp-util"   %%% "protocol-circe"           % Ver.webappUtil)
 // val webappUtilProtocolCirceTest = Def.setting("com.github.japgolly.webapp-util"   %%% "protocol-circe-test"      % Ver.webappUtil)
 // val webappUtilProtocolTest      = Def.setting("com.github.japgolly.webapp-util"   %%% "protocol-test"            % Ver.webappUtil)

    // Compiler plugins
    val betterMonadicFor = compilerPlugin("com.olegpy"     %% "better-monadic-for" % Ver.betterMonadicFor)
    val kindProjector    = compilerPlugin("org.typelevel"  %% "kind-projector"     % Ver.kindProjector cross CrossVersion.full)
  }
}
