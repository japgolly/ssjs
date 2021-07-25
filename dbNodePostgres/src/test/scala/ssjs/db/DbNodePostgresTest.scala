package ssjs.db

import org.scalatest.flatspec.AnyFlatSpec
import org.typelevel.discipline.scalatest.FlatSpecDiscipline
import org.scalatestplus.scalacheck.Checkers

class DbNodePostgresTest extends AnyFlatSpec with FlatSpecDiscipline with Checkers {
  checkAll("DbNodePostgresTest", DbTests(DbNodePostgres).db)
}
