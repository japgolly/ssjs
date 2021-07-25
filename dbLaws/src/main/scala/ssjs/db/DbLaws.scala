package ssjs.db

import cats.Eq
import org.scalacheck.Prop
import org.scalacheck.util.Pretty

object LawUtil {
  final case class IsEq[A](lhs: A, rhs: A)
  implicit final class IsEqArrow[A](private val lhs: A) extends AnyVal {
    def <->(rhs: A): IsEq[A] = IsEq(lhs, rhs)
  }

  implicit def catsLawsIsEqToProp[A](isEq: IsEq[A])(implicit ev: Eq[A], pp: A => Pretty): Prop =
    isEq match {
      case IsEq(x, y) =>
        if (ev.eqv(x, y)) Prop.proved
        else
          Prop.falsified :| {
            val exp = Pretty.pretty[A](y, Pretty.Params(0))
            val act = Pretty.pretty[A](x, Pretty.Params(0))
            s"Expected: $exp\n" + s"Received: $act"
          }
    }
}

import LawUtil._

// ===================================================================================================================

trait DbLaws {
  def db: Db

  def double(n: Int): IsEq[Int] =
    db.double(n) <-> (n * 2)
}

object DbLaws {
  def apply(implicit instance: Db): DbLaws =
    new DbLaws { override def db = instance }
}

// ===================================================================================================================

import org.typelevel.discipline.Laws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll

trait DbTests extends Laws {
  def laws: DbLaws

  def db(implicit arbI: Arbitrary[Int]): RuleSet =
    new SimpleRuleSet("db",
      "double" -> forAll(laws.double _),
    )
}

object DbTests {
  def apply(implicit instance: Db): DbTests =
    new DbTests { override val laws = DbLaws(instance) }
}
