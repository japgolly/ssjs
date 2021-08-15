package ssjs.db

import cats.syntax.apply._
import japgolly.scalajs.react.callback.AsyncCallback
import japgolly.scalajs.react.callback.CallbackCatsEffect._
import ssjs.base.test._
import utest._

// class DbNodePostgresTest extends LawTester {
//   checkAll("DbNodePostgresTest", DbTests(DbNodePostgres).db)
// }

object Test2 extends TestSuite {

  override def tests = Tests {

    "decoder" - {
      println()
      locally {
        val d = (Db.Type.Int.decoder, Db.Type.Str.decoder, Db.Type.Bool.decoder).tupled
        val r = d.decodeValues(List(Db.Value.Int(123), Db.Value.Str("x"), Db.Value.Bool(false)).iterator)
        println(r)
      }
      locally {
        val d = ((Db.Type.Int.decoder, Db.Type.Str.decoder).tupled, Db.Type.Bool.decoder).mapN { case ((a,b),c) => (a,b,c)}
        val r = d.decodeValues(List(Db.Value.Int(123), Db.Value.Str("x"), Db.Value.Bool(false)).iterator)
        println(r)
      }
      locally {
        val d = (Db.Type.Int.decoder, (Db.Type.Str.decoder, Db.Type.Bool.decoder).tupled).mapN { case (a,(b,c)) => (a,b,c)}
        val r = d.decodeValues(List(Db.Value.Int(123), Db.Value.Str("x"), Db.Value.Bool(false)).iterator)
        println(r)
      }
      println()
    }

/*
    "ah" - Node.asyncTest {
      val pg = Node.requireAs[PG]("pg")
      val c = PG.Client(pg, cfg => {
        cfg.host = "localhost"
        cfg.port = 18549
        cfg.database = "testdb"
        cfg.user = "testuser"
        cfg.password = "blah"
      })
      val q = PG.QueryConfig()
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
*/

    "ah2" - Node.asyncTest {
      val pg = Node.requireAs[PG]("pg")
      val c = PG.Client(pg, cfg => {
        cfg.host = "localhost"
        cfg.port = 18549
        cfg.database = "testdb"
        cfg.user = "testuser"
        cfg.password = "blah"
      })
      val db = DbNodePostgres[AsyncCallback](c)
      db.connection.use { conn =>
        for {
          s <- conn(Db.Call("select 1+1").single[Int])
          l <- conn(Db.Call("select 1+1 union select 123").list[Int])
        } yield {
          println()
          println(s)
          println(l)
          println()
        }
      }
    }
  }
}
