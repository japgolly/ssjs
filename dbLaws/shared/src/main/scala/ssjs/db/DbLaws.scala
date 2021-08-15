package ssjs.db

// import ssjs.base.laws._
import org.typelevel.discipline.Laws
// import org.scalacheck.Arbitrary
// import org.scalacheck.Prop.forAll

object DbLaws {
  def apply[F[_]](implicit instance: Db[F]): DbLaws[F] =
    new DbLaws[F] {
      override def db = instance
    }
}

trait DbLaws[F[_]] {
  def db: Db[F]

  // def double(n: Int): IsEq[Int] =
  //   db.double(n) <-> (n * 2)
}

trait DbTests[F[_]] extends Laws {
  def laws: DbLaws[F]

  def db/*(implicit arbI: Arbitrary[Int])*/: RuleSet =
    new SimpleRuleSet("db",
      // "double" -> forAll(laws.double _),
    )
}

object DbTests {
  def apply[F[_]](implicit instance: Db[F]): DbTests[F] =
    new DbTests[F] {
      override val laws = DbLaws(instance)
    }
}
