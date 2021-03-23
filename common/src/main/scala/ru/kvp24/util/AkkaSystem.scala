package ru.kvp24.util


import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext

object AkkaSystem {

  val akkaConfig: Config = ConfigFactory.load().getConfig("akka")

  implicit val system: ActorSystem = ActorSystem("http-service", akkaConfig)

  implicit val globalContext: ExecutionContext = ExecutionContext.global
}
