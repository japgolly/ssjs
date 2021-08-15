package ssjs.base.test

import org.scalatest.flatspec.AnyFlatSpec
import org.typelevel.discipline.scalatest.FlatSpecDiscipline
import org.scalatestplus.scalacheck.Checkers

trait LawTester extends AnyFlatSpec with FlatSpecDiscipline with Checkers
