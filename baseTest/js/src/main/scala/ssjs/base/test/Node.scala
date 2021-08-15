package ssjs.base.test

import japgolly.microlibs.testutil.TestUtil._
import japgolly.scalajs.react.callback.AsyncCallback
import scala.concurrent.Future
import scala.scalajs.js

object Node {

  @inline private def window = js.Dynamic.global.window
  @inline private def node   = window.node

  def requireAs[A](path: String): A =
    node.require(path).asInstanceOf[A]

  def require(path: String): js.Dynamic =
    requireAs[js.Dynamic](path)

  // private def envVar(name: String): js.UndefOr[String] =
  //   node.process.env.selectDynamic(name).asInstanceOf[js.UndefOr[String]]

  // private def envVarNeed(name: String): String =
  //   envVar(name).getOrElse(throw new RuntimeException("Missing env var: " + name))

  // private val inCI             = envVar("CI").contains("1")
  // private val asyncTestTimeout = if (inCI) 60000 else 3000
  private val asyncTestTimeout = 3000

  def asyncTest[A](ac: AsyncCallback[A]): Future[A] = {
    ac.timeoutMs(asyncTestTimeout).map {
      case Some(a) => a
      case None    => fail(s"Async test timed out after ${asyncTestTimeout / 1000} sec.")
    }.unsafeToFuture()
  }

}
