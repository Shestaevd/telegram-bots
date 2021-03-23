package ru.kvp24

import com.typesafe.config.ConfigFactory
import ru.kvp24.states.InitState
import ru.kvp24.util.Logger

object Launcher {

  val token: String = ConfigFactory.load().getConfig("bot").getString("token")
  val name: String = ConfigFactory.load().getConfig("bot").getString("name")

  def main(args: Array[String]): Unit = {

    Logger.info("im alive", name)

    Runtime.getRuntime.addShutdownHook(new Thread(() => Logger.info("oh no im died", name)))

    TelegramBot.create(token, name, InitState())
  }

}
