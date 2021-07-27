package ssjs.db

import japgolly.scalajs.react.callback.AsyncCallback
import ssjs.base.test._

class DbNodePostgresTest extends LawTester {
  checkAll("DbNodePostgresTest", DbTests(DbNodePostgres).db)
}

import utest._
import scalajs.js

object Test2 extends TestSuite {

  override def tests = Tests {
    "ah" - Node.asyncTest {
      val pg = Node.requireAs[PG]("pg")
      val c = PG.Client(pg, cfg => {
        cfg.host = "localhost"
        cfg.port = 18549
        cfg.database = "testdb"
        cfg.user = "testuser"
        cfg.password = "blah"
      })
      val q = js.Dynamic.literal().asInstanceOf[PG.QueryConfig]
      q.text = "select 1+1"
      q.rowMode = js.defined("array")
      for {
        _ <- AsyncCallback.fromJsPromise(c.connect())
        a <- AsyncCallback.fromJsPromise(c.query(q))
      } yield {
        org.scalajs.dom.console.log(a.fields)
        org.scalajs.dom.console.log(a.rows)
        org.scalajs.dom.console.log(a.rows(0))
        org.scalajs.dom.console.log(a.rows(0).asInstanceOf[js.Array[Any]](0))
        val result = a.rows(0).asInstanceOf[js.Array[Any]](0).asInstanceOf[Int]
        org.scalajs.dom.console.log(result)
      }
    }
  }
}