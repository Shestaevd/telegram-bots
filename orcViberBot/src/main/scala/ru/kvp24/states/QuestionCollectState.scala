package ru.kvp24.states

import ru.kvp24.CacheStore
import ru.kvp24.http.UserReplyCommon
import ru.kvp24.messages.{IncomingViberMessage, OutgoingViberMessage}
import ru.kvp24.stateMachine.{Reply, State}
import ru.kvp24.util.Mailer
import ru.kvp24.util.AkkaSystem._

import scala.concurrent.Future
import scala.util.Try

class QuestionCollectState(phone: String, email: String)
    extends State[IncomingViberMessage, OutgoingViberMessage] {
  override def handle(
      message: IncomingViberMessage
  ): Option[Reply[IncomingViberMessage, OutgoingViberMessage]] = {
    Try[Option[Reply[IncomingViberMessage, OutgoingViberMessage]]] {
      val reply =
        "Благодарим вас за обращение. Мы перезвоним вам в рабочее время."

      Future {
        CacheStore.scheduler.drop(message.key)

        Mailer.sendEmail(
          email,
          "Обращение из Viber bot",
          s"""ФИО - ${message.incomingMessage.sender.name}
           |Телефон - $phone
           |Email - $email
           |Обращение - ${message.incomingMessage.message.text}
           |""".stripMargin
        )
      }

      Some(
        Reply(
          OutgoingViberMessage(
            UserReplyCommon(
              message.incomingMessage.sender.id,
              message.incomingMessage.sender.api_version,
              reply
            )
          ),
          FinishState()
        )
      )
    }
  }.toOption.flatten
}

object QuestionCollectState {
  def apply(phone: String, email: String): QuestionCollectState =
    new QuestionCollectState(phone: String, email: String)
}
