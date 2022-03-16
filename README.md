# ⚠️ This is not a real project

This was a quick experiment that I decided to abandon early on.
From memory, the concept was proven successful but I decided I couldn't afford the amount of time and effort to do it properly.

The dream was to have a cross-platform API for an entire web backend so that one could switch from a JVM-based server to running on lambda via configuration and deployment, ideally with no changes to code.

[Here are my old notes for the vision of the project.](./serverless.md)


# Cross-platform DB PoC

* [API](./dbApi/shared/src/main/scala/ssjs/db/Db.scala) - A cross-platform DB API. Similar to the Doobie interface.

* [Laws](./dbLaws/shared/src/main/scala/ssjs/db/DbLaws.scala) - Laws that could test an DB API implementation. Currently has no laws (lol) but *does* have a commented-out test that would test a `SELECT $n * 2` query that I saw working from Node before I commented it out.

* JS/Node impl: [PG facade](./dbNodePostgres/src/main/scala/ssjs/db/PG.scala) & [Scala impl](./dbNodePostgres/src/main/scala/ssjs/db/DbNodePostgres.scala) plus a [test of its own](./dbNodePostgres/src/test/scala/ssjs/db/DbNodePostgresTest.scala)

* JVM impl: use your imagination! I would've backed it by Doobie or maybe even just JDBC directly
