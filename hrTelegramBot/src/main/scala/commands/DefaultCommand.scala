package commands

import com.typesafe.config.ConfigFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import ru.kvp24.util.{Logger, Mailer}

import scala.util.{Failure, Success, Try}

case class DefaultCommand() extends Command {

  val hrEmail: String = ConfigFactory.load().getConfig("hr").getString("email")
  val botName: String = ConfigFactory.load().getConfig("bot").getString("name")

  override def handle(message: Message): SendMessage = {
    val sendMessage = new SendMessage()
    val messageReply = message.getText
    sendMessage.setChatId(message.getChatId.toString)
    Try(
      Mailer.sendEmail(hrEmail, "Запрос из HrTelegramBot",
        s"""
           |Имя - ${message.getFrom.getLastName} ${message.getFrom.getFirstName}
           |UserId - ${message.getFrom.getId}
           |Комментарий - $messageReply
           |""".stripMargin)) match {
      case Failure(exception) =>
        exception.printStackTrace()
        Logger.error(s"Error occurred during transfer user data to email ${exception.getLocalizedMessage} \n comment - $messageReply", botName)
        sendMessage.setText("Приносим извинения, но произошла ошибка на стороне сервера.")
      case Success(_) =>
        sendMessage.setText("Ваше обращение принято")
    }
    sendMessage
  }
}
