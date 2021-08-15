name      := "SSJS"
startYear := Some(2021)

// ThisBuild / organization     := "xxx"
// ThisBuild / organizationName := "xxx"
ThisBuild / shellPrompt      := ((s: State) => Project.extract(s).currentRef.project + "> ")

val baseLawsJS      = Build.baseLawsJS
val baseLawsJVM     = Build.baseLawsJVM
val baseTestJS      = Build.baseTestJS
val baseTestJVM     = Build.baseTestJVM
val baseTestLawsJS  = Build.baseTestLawsJS
val baseTestLawsJVM = Build.baseTestLawsJVM
val dbApiJS         = Build.dbApiJS
val dbApiJVM        = Build.dbApiJVM
val dbLawsJS        = Build.dbLawsJS
val dbLawsJVM       = Build.dbLawsJVM
val dbNodePostgres  = Build.dbNodePostgres
val root            = Build.root
