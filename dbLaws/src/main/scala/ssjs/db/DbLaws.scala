package ssjs.db

import ssjs.base.laws._
import org.typelevel.discipline.Laws
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll

object DbLaws {
  def apply(implicit instance: Db): DbLaws =
    new DbLaws { override def db = instance }
}

trait DbLaws {
  def db: Db

  def double(n: Int): IsEq[Int] =
    db.double(n) <-> (n * 2)
}

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
