package ru.kvp24.states

import com.typesafe.config.ConfigFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kvp24.CacheStore
import ru.kvp24.messages.{IncomingUpdate, OutgoingSendMessage}
import ru.kvp24.stateMachine.{Reply, State}
import ru.kvp24.util.Mailer

class QuestionCollectState(phone: String, mail: String) extends State[IncomingUpdate, OutgoingSendMessage] {

  val email: String = ConfigFactory.load().getConfig("orc").getString("email")

  override def handle(incomingUpdate: IncomingUpdate): Option[Reply[IncomingUpdate, OutgoingSendMessage]] = {

    val update = incomingUpdate.incomingMessage

    Option(update.getMessage).flatMap(msg => Option(msg.getText)).map { text =>
      val reply = """Обращение принято. До связи :smiley:"""

      val sendMessage = new SendMessage()
      sendMessage.setText(reply)

      CacheStore.scheduler.drop(incomingUpdate.key)

      Mailer.sendEmail(
        email,
        "Обращение из Telegram bot",
        s"""ФИО - ${update.getMessage.getFrom.getLastName} ${update.getMessage.getFrom.getFirstName}
           |Телефон - $phone
           |Email - $mail
           |Обращение - $text
           |""".stripMargin
      )

      Reply(OutgoingSendMessage(sendMessage), FinishState())
    }
  }
}

object QuestionCollectState {
  def apply(phone: String, email: String): QuestionCollectState =
    new QuestionCollectState(phone: String, email: String)
}
