package ssjs.db

import ssjs.base.test.LawTester

class DbNodePostgresTest extends LawTester {
  checkAll("DbNodePostgresTest", DbTests(DbNodePostgres).db)
}
