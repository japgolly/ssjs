name      := "SSJS"
startYear := Some(2021)

// ThisBuild / organization     := "xxx"
// ThisBuild / organizationName := "xxx"
ThisBuild / shellPrompt      := ((s: State) => Project.extract(s).currentRef.project + "> ")

val root           = Build.root
val dbApi          = Build.dbApi
val dbLaws         = Build.dbLaws
val dbNodePostgres = Build.dbNodePostgres