addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.2")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.2")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.12")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

//addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.8.2")

addSbtPlugin("com.typesafe.sbt"    % "sbt-native-packager" % "1.7.0")

resolvers := Seq(
  "Nexus Repo".at("http://nexus.itecos.com/content/repositories/k24cluster").withAllowInsecureProtocol(true)
)
