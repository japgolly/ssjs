package ssjs.db

import cats.{Apply, Functor}
import cats.effect.Resource
import cats.syntax.functor._
import scala.collection.Factory
import scala.collection.immutable.ArraySeq
import scala.reflect.ClassTag

trait Db[F[_]] {
  import Db._

  def connection: Resource[F, Connection[F]]
}

object Db {

  trait Connection[F[_]] {
    protected implicit def functor: Functor[F]

    def rawCall(sql: String): F[Results[Row]]

    final def apply[O](c: Call[O]): F[O] =
      rawCall(c.sql).map(c.recv)
  }

  abstract class Results[A] { self =>

    override def toString = s"Db.Results(${iterator().size} rows)"

    def iterator(): Iterator[A]

    final def mapRows[B](f: A => B): Results[B] =
      new Results[B] {
        override def iterator() = self.iterator().map(f)
      }

    final def unsafeSingle(): A = {
      val it = iterator()
      if (it.isEmpty)
        throw new RuntimeException("No rows available.")
      else
        it.next()
    }

    final def option: Option[A] =
      iterator().nextOption()

    final def to[F[_]](implicit f: Factory[A, F[A]]): F[A] =
      f.fromSpecific(iterator())

    final def arraySeq(implicit c: ClassTag[A]): ArraySeq[A] =
      to[ArraySeq]

    final def list: List[A] =
      to[List]

    final def vector: Vector[A] =
      to[Vector]
  }

  object Results {
    def apply[A](f: => Iterator[A]): Results[A] =
      new Results[A] {
        override def iterator() = f
      }
  }

  trait Row {
    override def toString = s"Db.Row(${iterator().size} columns)"
    def iterator(): Iterator[Value]
  }

  object Row {
    def apply(f: => Iterator[Value]): Row =
      new Row {
        override def iterator() = f
      }
  }

  // ===================================================================================================================

  sealed trait Type[A]
  object Type {

    sealed trait InResult[A] extends Type[A] {
      val decoder: Decoder[A]
    }

    trait ReadByType[A] { self: InResult[A] =>

      final val decodeValue: Value => Decoder.Result[A] =
        v =>
          if (v.typ eq this)
            Right(v.value.asInstanceOf[A])
          else
            Left(Decoder.Error(s"Expected $this, got ${v.value} of type $v"))

      override val decoder: Decoder[A] =
        Decoder.next(decodeValue)
    }

    case object Bool extends InResult[Boolean] with ReadByType[Boolean]
    case object Int  extends InResult[Int]     with ReadByType[Int]
    case object Str  extends InResult[String]  with ReadByType[String]
  }

  // ===================================================================================================================

  sealed trait Value {
    type A
    val value: A
    val typ: Type[A]
  }
  object Value {
    type Of[B] = Value { type A = B }

    sealed abstract class Adaptor[B](final val typ: Type[B]) extends Value {
      override final type A = B
    }

    final case class Bool(value: Boolean)   extends Adaptor(Type.Bool)
    final case class Int (value: scala.Int) extends Adaptor(Type.Int)
    final case class Str (value: String)    extends Adaptor(Type.Str)

    def unsafeFromAny(a: Any): Value =
      a match {
        case v: Boolean   => Bool(v)
        case v: scala.Int => Int (v)
        case v: String    => Str (v)
      }
  }

  // ===================================================================================================================

  final case class Decoder[A](decodeValues: Iterator[Value] => Decoder.Result[A]) {

    val decodeRowOrThrow: Row => A =
      r =>
        try
          decodeValues(r.iterator()) match {
            case Right(a) => a
            case Left(e) => throw e
          }
        catch {
          case e: Decoder.Error => throw e
          case t: Throwable     => throw Decoder.Error(t.getMessage, Some(t))
        }

    def map[B](f: A => B): Decoder[B] =
      Decoder(decodeValues(_).map(f))
  }

  object Decoder {

    type Result[+A] = Either[Error, A]

    final case class Error(reason: String, cause: Option[Throwable] = None) extends RuntimeException(reason, cause.orNull)

    def next[A](parse: Value => Result[A]): Decoder[A] =
      apply[A] { it =>
        if (it.isEmpty)
          Left(Error("Attempted to read beyond available number of columns."))
        else
          parse(it.next())
      }

    implicit def bool: Decoder[Boolean] = Type.Bool.decoder
    implicit def int: Decoder[Int] = Type.Int.decoder
    implicit def str: Decoder[String] = Type.Str.decoder

    implicit lazy val catsApply: Apply[Decoder] =
      new Apply[Decoder] {

        override def map[A, B](fa: Decoder[A])(f: A => B): Decoder[B] =
          fa.map(f)

        override def ap[A, B](ff: Decoder[A => B])(fa: Decoder[A]): Decoder[B] =
          Decoder[B] { it =>
            for {
              f <- ff.decodeValues(it)
              a <- fa.decodeValues(it)
            } yield f(a)
          }
      }
  }

  // ===================================================================================================================

  final class Call[+O](val sql: String, val recv: Results[Row] => O)

  object Call {
    def apply(sql: String) =
      new DslOut(sql)

    final class DslOut(private val sql: String) extends AnyVal {
      private def build[A, O](d: Decoder[A])(f: Results[A] => O): Call[O] =
        new Call(sql, r => f(r.mapRows(d.decodeRowOrThrow)))

      def raw                              : Call[Results[Row]] = new Call(sql, identity)
      def single[A](implicit d: Decoder[A]): Call[A]            = build(d)(_.unsafeSingle())
      def option[A](implicit d: Decoder[A]): Call[Option[A]]    = build(d)(_.option)
      def list  [A](implicit d: Decoder[A]): Call[List  [A]]    = build(d)(_.list)
    }
  }

}
