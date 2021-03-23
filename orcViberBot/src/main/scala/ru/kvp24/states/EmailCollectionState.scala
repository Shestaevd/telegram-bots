package ru.kvp24.states

import ru.kvp24.CacheStore
import ru.kvp24.http.UserReplyCommon
import ru.kvp24.messages.{IncomingViberMessage, OutgoingViberMessage}
import ru.kvp24.stateMachine.{Reply, State}
import ru.kvp24.util.Mailer

import scala.concurrent.duration.DurationInt

class EmailCollectionState(phone: String) extends State[IncomingViberMessage, OutgoingViberMessage]{
  override def handle(message: IncomingViberMessage): Option[Reply[IncomingViberMessage, OutgoingViberMessage]] = {

    message.incomingMessage.message.text match {
      case email if email.matches("^(.+)@(.+)$") || email.equals("email_refuse") =>

        val reply = "Напишите ваше обращение"

        CacheStore.scheduler.update(message.key, 15.minutes)(
          Mailer.sendEmail(
            email,
            "Обращение из Viber bot",
            s"""ФИО - ${message.incomingMessage.sender.name}
               |Телефон - $phone
               |Email - $email
               |""".stripMargin
          )
        )

        Some(Reply(OutgoingViberMessage(UserReplyCommon(message.incomingMessage.sender.id, message.incomingMessage.sender.api_version, reply)), QuestionCollectState(phone, email)))
      case _ =>
        val reply = "Формат указанного email неверен"
        Some(Reply(OutgoingViberMessage(UserReplyCommon(message.incomingMessage.sender.id, message.incomingMessage.sender.api_version, reply)), EmailCollectionState(phone)))
    }
  }
}

object EmailCollectionState {
  def apply(phone: String): EmailCollectionState = new EmailCollectionState(phone)
}
