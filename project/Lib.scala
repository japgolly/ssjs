import sbt._
import sbt.Keys._
import com.timushev.sbt.updates.UpdatesPlugin.autoImport._
import com.typesafe.sbt.GitPlugin.autoImport._
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.linker.interface.{CheckedBehavior, Semantics}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scala.{Console => C}
import scala.concurrent.duration._
import scalafix.sbt.ScalafixPlugin
import scalafix.sbt.ScalafixPlugin.autoImport._

object Lib {
  import Dependencies._

  private val cores = java.lang.Runtime.getRuntime.availableProcessors()

  private def readConfigVar(name: String): String =
    Option(System.getProperty(name)).orElse(Option(System.getenv(name)))
      .fold("")(_.trim.toLowerCase)

  val releaseMode = readConfigVar("MODE") == "release"
  if (releaseMode) {
    println(s"[info] ${C.RED_B}${C.WHITE}Release Mode.${C.RESET}")
  }
  def devMode = !releaseMode

  val emitSourceMapsValue = readConfigVar("emitSourceMaps") == "1"
  if (emitSourceMapsValue) {
    println("[info] \u001b[1;93mSource maps enabled.\u001b[0m")
  }

  def defaultScalacFlags = Seq(
    "-deprecation",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-unchecked",                                    // Enable additional warnings where generated code depends on assumptions.
    "-Wconf:msg=may.not.be.exhaustive:e",            // Make non-exhaustive matches errors instead of warnings
    "-Wdead-code",                                   // Warn when dead code is identified.
    "-Wunused:explicits",                            // Warn if an explicit parameter is unused.
    "-Wunused:implicits",                            // Warn if an implicit parameter is unused.
    "-Wunused:imports",                              // Warn if an import selector is not referenced.
    "-Wunused:locals",                               // Warn if a local definition is unused.
    "-Wunused:nowarn",                               // Warn if a @nowarn annotation does not suppress any warnings.
    "-Wunused:patvars",                              // Warn if a variable bound in a pattern is unused.
    "-Wunused:privates",                             // Warn if a private member is unused.
    "-Xlint:adapted-args",                           // An argument list was modified to match the receiver.
    "-Xlint:constant",                               // Evaluation of a constant arithmetic expression resulted in an error.
    "-Xlint:delayedinit-select",                     // Selecting member of DelayedInit.
    "-Xlint:deprecation",                            // Enable -deprecation and also check @deprecated annotations.
    "-Xlint:eta-zero",                               // Usage `f` of parameterless `def f()` resulted in eta-expansion, not empty application `f()`.
    "-Xlint:implicit-not-found",                     // Check @implicitNotFound and @implicitAmbiguous messages.
    "-Xlint:inaccessible",                           // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                              // A type argument was inferred as Any.
    "-Xlint:missing-interpolator",                   // A string literal appears to be missing an interpolator id.
    "-Xlint:nonlocal-return",                        // A return statement used an exception for flow control.
    "-Xlint:nullary-unit",                           // `def f: Unit` looks like an accessor; add parens to look side-effecting.
    "-Xlint:option-implicit",                        // Option.apply used an implicit view.
    "-Xlint:poly-implicit-overload",                 // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",                         // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                            // In a pattern, a sequence wildcard `_*` should match all of a repeated parameter.
    "-Xlint:valpattern",                             // Enable pattern checks in val definitions.
    "-Xmixin-force-forwarders:false",                // Only generate mixin forwarders required for program correctness.
    "-Xno-forwarders",                               // Do not generate static forwarders in mirror classes.
    "-Xsource:2.13",
    "-Ybackend-parallelism", cores.min(16).toString,
    "-Ycache-macro-class-loader:last-modified",
    "-Ycache-plugin-class-loader:last-modified",
    "-Yimports:java.lang,scala,cryptofolio.Predef",  // Use custom Predef
    "-Yjar-compression-level", "9",                  // compression level to use when writing jar files
    "-Yno-generic-signatures",                       // Suppress generation of generic signatures for Java.
    "-Ypatmat-exhaust-depth", "off"
  ) ++ (
    if (devMode) Seq(
      "-Xcheckinit",
    ) else Seq(
      "-opt-inline-from:**",
      "-opt:l:inline",
      "-opt:l:method",
      "-Xdisable-assertions",
      "-Xelide-below", "OFF",
    )
  )

  private def defaultSettings: Project => Project = _
    .enablePlugins(ScalafixPlugin)
    .settings(
      scalacOptions              ++= defaultScalacFlags,
      scalaVersion                := Ver.scala,
      testFrameworks              := List(new TestFramework("utest.runner.Framework")),
      update / aggregate          := true,
      updateOptions               := updateOptions.value.withCachedResolution(true),
      addCompilerPlugin(Dep.betterMonadicFor),
      addCompilerPlugin(Dep.kindProjector),
    )

  private def testSettings(enabled: Boolean): Project => Project =
    if (enabled)
      _.settings(
        libraryDependencies ++= Seq(
          Dep.microlibsTestUtil.value % Test,
          Dep.utest            .value % Test,
        ),
      )
    else
      _.settings(test := {})

  sealed trait JsTestType
  case object NoTests             extends JsTestType
  case object TestJsWithNode      extends JsTestType
  // case object TestJsWithPhantomJs extends JsTestType

  private def jsTestSettings(t: JsTestType): Project => Project =
    t match {
      case NoTests =>
        testSettings(enabled = false)
      case TestJsWithNode =>
        _.configure(testSettings(enabled = true)).settings(
          Test / jsEnv := new JSDOMNodeJSEnv(JSDOMNodeJSEnv.Config()),
        )
      // case TestJsWithPhantomJs =>
      //   _.configure(testSettings(enabled = true)).settings(
      //     Test / scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) },
      //     Test / jsEnv := PhantomJSEnv().value,
      //     Test / jsEnvInput := Input.Script(((ThisBuild / baseDirectory).value / "project/phantomjs-fix.js").toPath) +: (Test / jsEnvInput).value,
      //   )
    }

  def defaultJvmSettings(tests: Boolean = true): Project => Project = _
    .configure(defaultSettings)
    .configure(testSettings(enabled = tests))

  private def defaultJsSettingsProd: Project => Project = _
    .settings(
      scalaJSStage := FullOptStage,
      scalaJSLinkerConfig ~= { _
        .withSemantics(_
          .withRuntimeClassNameMapper(Semantics.RuntimeClassNameMapper.discardAll())
          .withArrayIndexOutOfBounds(CheckedBehavior.Unchecked)
          .withAsInstanceOfs(CheckedBehavior.Unchecked)
          .withProductionMode(true)
        )
        .withESFeatures(_
          // Choose to be slow on Firefox but much smaller JS size
          // See https://www.scala-js.org/news/2020/11/16/announcing-scalajs-1.3.1/
          .withAvoidClasses(false)
        )
        .withPrettyPrint(false)
        .withClosureCompiler(true)
        .withCheckIR(true)
      },
      // More than 1 running instance of Google Closure exponentially increases time & mem-usage
      Global / concurrentRestrictions += Tags.limit(ScalaJSTags.Link, 1)
    )

  def defaultJsSettings(t: JsTestType): Project => Project = _
    .configure(defaultSettings)
    .configure(if (devMode) identity else defaultJsSettingsProd)
    .configure(jsTestSettings(t))

  // def defaultSettings(t: JsTestType): CrossProject => CrossProject = _
  //   .jvmConfigure(defaultJvmSettings(tests = (t != NoTests)))
  //   .jsConfigure(defaultJsSettings(t))

  /** This doesn't work when fork := true */
  def invokeAfterTests(objectName: String, methodName: String) =
    Test / testOptions += Tests.Cleanup(invokeMethod(objectName, methodName, _))

  /** This doesn't work when fork := true */
  private def invokeMethod(objectName: String, methodName: String, loader: ClassLoader): Unit = {
    import scala.util._
    def Try2[A](a: => A) = {
      val t = try Success(a) catch { case e: Throwable => Failure(e) }
      // println(t)
      t
    }
    for {
      objC <- Try2(loader.loadClass(objectName + "$"))
      clsC <- Try2(loader.loadClass(objectName))
      objM <- Try2(objC.getField("MODULE$"))
      clsM <- Try2(clsC.getDeclaredMethod(methodName))
      objI <- Try2(objM.get(null))
      _    <- Try2(clsM.invoke(objI))
    } yield ()
  }

}
