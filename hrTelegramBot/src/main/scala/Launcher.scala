import com.typesafe.config.ConfigFactory
import commands.{DefaultCommand, StartCommand}
import ru.kvp24.util.Logger

object Launcher {
  def main(args: Array[String]): Unit = {

    val token = ConfigFactory.load().getConfig("bot").getString("token")
    val name = ConfigFactory.load().getConfig("bot").getString("name")

    Logger.info("im alive", name)

    Runtime.getRuntime.addShutdownHook(new Thread(() => Logger.info("oh no im died", name)))

    TelegramBot.create(token, name, DefaultCommand(), StartCommand())

  }
}
