package ssjs.db

import cats.effect.{Async, Resource}
import cats.syntax.functor._
import scala.scalajs.js
import scala.scalajs.js.|
import scala.collection.AbstractIterator

final class DbNodePostgres[F[_]](pg: PG.Client)(implicit F: Async[F]) extends Db[F] {
  import DbNodePostgres._

  private val pgClient: Resource[F, PG.Client] =
    Resource[F, PG.Client](promise(pg.connect()).map(_ => (pg, promise(pg.end()))))

  override def connection =
    pgClient.as(new Connection(pg))
}

object DbNodePostgres {

  def apply[F[_]: Async](pg: PG.Client): DbNodePostgres[F] =
    new DbNodePostgres(pg)

  final class Connection[F[_]](pg: PG.Client)(implicit F: Async[F]) extends Db.Connection[F] {
    override protected implicit def functor = F

    override def rawCall(sql: String): F[Db.Results[Db.Row]] =
      F.async[Db.Results[Db.Row]] { k =>
        F.delay {
          val q = PG.QueryConfig()
          q.text = sql
          q.rowMode = js.defined("array")
          completePromiseBy(pg.query(q), k)(parseResults)
          None
        }
      }

    private val parseResults: PG.Result => Db.Results[Db.Row] = pg => {
      val colCount = pg.fields.length

      def parseRow(r: PG.Row): Db.Row = {
        val cols = r.asInstanceOf[PG.ArrayRow]
        Db.Row {
          new AbstractIterator[Db.Value] {
            private[this] var n = 0
            override def hasNext = n < colCount
            override def next() = {
              val a = cols(n)
              n += 1
              Db.Value.unsafeFromAny(a)
            }
          }
        }
      }

      Db.Results {
        new AbstractIterator[Db.Row] {
          private[this] var n = 0
          override def hasNext = n < pg.rowCount
          override def next() = {
            val r = pg.rows(n)
            n += 1
            parseRow(r)
          }
        }
      }
    }
  }

  private[db] def completePromise[A](p: js.Promise[A])(k: Either[Throwable, A] => Unit): Unit = {
    val onFulfilled: js.Function1[A, Unit | js.Thenable[Unit]] =
      a => k(Right(a))

    val onRejected : js.Function1[scala.Any, Unit | js.Thenable[Unit]] =
      _ match {
        case t: Throwable => k(Left(t))
        case a            => k(Left(js.JavaScriptException(a)))
      }

    p.`then`[Unit](onFulfilled, onRejected)
  }

  private[db] def completePromiseBy[A, B](p: js.Promise[A], k: Either[Throwable, B] => Unit)(f: A => B): Unit = {
    val onFulfilled: js.Function1[A, B | js.Thenable[B]] = f(_)
    completePromise(p.`then`(onFulfilled))(k)
  }

  private[db] def promise[F[_], A](p: => js.Promise[A])(implicit F: Async[F]): F[A] =
    F.async[A] { k =>
      F.delay {
        completePromise(p)(k)
        None
      }
    }
}
