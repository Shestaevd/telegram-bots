package ru.kvp24

import com.typesafe.config.ConfigFactory
import ru.kvp24.bot.Bot
import ru.kvp24.http.{Marshaller, Server}

import scala.concurrent.ExecutionContext

object Launcher extends Marshaller{

  val token: String = ConfigFactory.load().getConfig("bot").getString("token")
  val name: String = ConfigFactory.load().getConfig("bot").getString("name")

  def main(args: Array[String]): Unit = {

    val (_, bind) = Bot(Server("10.255.100.146", 8081, token))

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        bind.flatMap(_.unbind())(ExecutionContext.global)
        println("im stopped")
      }
    })

  }

}