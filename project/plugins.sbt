addSbtPlugin("ch.epfl.scala"      % "sbt-scalafix"              % "0.9.29")
addSbtPlugin("com.timushev.sbt"   % "sbt-updates"               % "0.6.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-git"                   % "1.0.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"               % "1.7.0")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
