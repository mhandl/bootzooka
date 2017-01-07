addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.heroku" % "sbt-heroku" % "1.0.0")

addSbtPlugin("com.updateimpact" % "updateimpact-sbt-plugin" % "2.1.1")

// taken from https://github.com/outworkers/phantom/issues/520
def websudosPattern = {
  val pList = List("[organisation]/[module](_[scalaVersion])(_[sbtVersion])/[revision]/[artifact]-[revision](-[classifier]).[ext]")
  Patterns(pList, pList, true)
}

resolvers ++= Seq(
  Resolver.url("Maven ivy Websudos", url(Resolver.DefaultMavenRepositoryRoot))(websudosPattern)
)

addSbtPlugin("com.websudos" %% "phantom-sbt" % "1.27.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.0.1")
//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.4.0")
