package ru.kvp24.states

import com.typesafe.config.ConfigFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kvp24.{CacheStore, stateMachine}
import ru.kvp24.messages.{IncomingUpdate, OutgoingSendMessage}
import ru.kvp24.stateMachine.{Reply, State}
import ru.kvp24.util.Mailer

import scala.concurrent.duration.DurationInt

class EmailCollectState(phone: String) extends State[IncomingUpdate, OutgoingSendMessage] {

  val email: String = ConfigFactory.load().getConfig("orc").getString("email")

  override def handle(incomingUpdate: IncomingUpdate): Option[Reply[IncomingUpdate, OutgoingSendMessage]] = {
    val update = incomingUpdate.incomingMessage
    val sendMessage = new SendMessage()

    Option(update.getMessage)
      .map(_.getText)
      .orElse(Option(update.getCallbackQuery).map(_.getData))
      .map {
        case mail if mail.matches("^(.+)@(.+)$") || Option(update.getCallbackQuery).exists(_.getData.equals("email_refused")) =>

          val reply = "Напишите ваше обращение"
          sendMessage.setText(reply)

          CacheStore.scheduler.update(incomingUpdate.key, 15.minutes)(
            Mailer.sendEmail(
              email,
              "Обращение из Telegram bot",
              s"""ФИО - ${update.getMessage.getFrom.getLastName} ${update.getMessage.getFrom.getFirstName}
                 |Телефон - $phone
                 |Email - $mail
                 |""".stripMargin
            )
          )

          stateMachine.Reply(OutgoingSendMessage(sendMessage), QuestionCollectState(phone, mail))

        case _ =>
          val reply = "Формат указанного email неверен"
          sendMessage.setText(reply)
          stateMachine.Reply(OutgoingSendMessage(sendMessage), EmailCollectState(phone))
    }
  }
}

object EmailCollectState {
  def apply(phone: String): EmailCollectState = new EmailCollectState(phone)
}
