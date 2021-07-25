package ssjs.db

object DbNodePostgres extends Db {
  override def double(n: Int) = n * 2
}
