import com.typesafe.sbt.packager.docker.DockerChmodType

lazy val core = (project in file("."))
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    common,
    hrTelegramBot,
    orcTelegramBot,
    orcViberBot
  )

lazy val common = (project in file("common"))
  .settings(globalSettings)
  .settings(libraryDependencies ++= commonDependencies)
  .settings(libraryDependencies ++= dependencies.akka)
  .settings(libraryDependencies ++= dependencies.cache)
  .settings(libraryDependencies += dependencies.mailer)
  .disablePlugins(AssemblyPlugin)

lazy val hrTelegramBot = (project in file("hrTelegramBot"))
  .settings(globalSettings)
  .settings(ThisBuild / name := "HrTelegramBot")
  .settings(mainClass in (Compile, run) := Some("Launcher"))
  .settings(libraryDependencies ++= dependencies.cache)
  .settings(mainClass in assembly := Some("Launcher"))
  .settings(libraryDependencies += dependencies.telegram)
  .dependsOn(common)

lazy val orcTelegramBot = (project in file("orcTelegramBot"))
  .settings(globalSettings)
  .settings(ThisBuild / name := "TelegramBot")
  .settings(mainClass in (Compile, run) := Some("ru.kvp24.Launcher"))
  .settings(mainClass in assembly := Some("ru.kvp24.Launcher"))
  .settings(libraryDependencies += dependencies.telegram)
  .settings(assemblyJarName in assembly := "orcTelegramBot")
  .settings(assemblyConfig)
  .settings(dockerSettings)
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .dependsOn(common)

lazy val orcViberBot = (project in file("orcViberBot"))
  .settings(globalSettings)
  .settings(ThisBuild / name := "ViberBot")
  .settings(mainClass in (Compile, run) := Some("ru.kvp24.Launcher"))
  .settings(mainClass in assembly := Some("ru.kvp24.Launcher"))
  .settings(libraryDependencies += dependencies.spray)
  .settings(libraryDependencies ++= dependencies.akka)
  .settings(assemblyConfig)
  .dependsOn(common)

lazy val globalSettings = Seq(
  ThisBuild / organization := "ru.kvp24.com",
  ThisBuild / scalaVersion := "2.13.4",
  version := "0.4",
)

lazy val dependencies = new {
  private val telegramV = "5.0.1"
  private val slf4jV = "1.6.1"
  private val configTypeSafeV = "1.0.2"

  private val AkkaV = "2.6.8"
  private val AkkaHttpV = "10.2.3"

  private val scaffeineV = "4.0.2"
  private val caffeineV = "3.0.0"

  private val mailerV = "1.4"

  private val sprayV = "10.2.4"

  val telegram = "org.telegram" % "telegrambots" % telegramV
  val slf4j = "org.slf4j" % "slf4j-simple" % slf4jV

  val configTypeSafe = "com.typesafe" % "config" % configTypeSafeV

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaV,
    "com.typesafe.akka" %% "akka-stream" % AkkaV,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpV
  )

  val cache = Seq(
    "com.github.ben-manes.caffeine" % "caffeine" % caffeineV,
    "com.github.blemale" %% "scaffeine" % scaffeineV % "compile"
  )

  val mailer = "javax.mail" % "mail" % mailerV

  val spray = "com.typesafe.akka" %% "akka-http-spray-json" % sprayV
}

lazy val commonDependencies = Seq(
  dependencies.slf4j,
  dependencies.configTypeSafe
)

lazy val assemblyConfig = Seq(
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case PathList("module-info.class") => MergeStrategy.discard
    case path if path.contains("activation") => MergeStrategy.first
    case "application.conf" => MergeStrategy.concat
    case keep => (assemblyMergeStrategy in assembly).value(keep)
  }
)

lazy val nexus = Seq(
  "Nexus Repo".at("http://nexus.itecos.com/content/repositories/k24cluster").withAllowInsecureProtocol(true),
)

lazy val dockerSettings = Seq(
  packageName in Docker := "kvp24/orc-telegram-bot",
  dockerUpdateLatest := true,
  dockerBaseImage := "adoptopenjdk/openjdk15",
  dockerRepository := Some("nexus.itecos.com:5001"),
  daemonUserUid in Docker := None,
  daemonUser in Docker := "root",
  dockerUpdateLatest := true,
  dockerChmodType := DockerChmodType.UserGroupWriteExecute,
  publishTo := Some("Nexus".at("http://nexus.itecos.com/content/repositories/k24cluster").withAllowInsecureProtocol(true)),
  resolvers := nexus,
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "nexus.itecos.com",
    "admin",
    "admin123"
  )
)