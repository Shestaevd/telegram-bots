package ru.kvp24.states
import ru.kvp24.http.UserReplyCommon
import ru.kvp24.messages.{IncomingViberMessage, OutgoingViberMessage}
import ru.kvp24.stateMachine.{Reply, State}

import scala.util.Try

class InitState extends State[IncomingViberMessage, OutgoingViberMessage] {
  override def handle(
      message: IncomingViberMessage
  ): Option[Reply[IncomingViberMessage, OutgoingViberMessage]] = {
    Try[Option[Reply[IncomingViberMessage, OutgoingViberMessage]]] {
      val receive = message.incomingMessage

      val reply =
        """Уважаемый посетитель, к сожалению, вы написали нам в не рабочее время.
          |Вы можете отправить запрос на нашу электронную почту client@kvp24.ru или позвонить нам в рабочее время по бесплатному номеру 8-800-333-23-40
          |Мы работаем с понедельника по пятницу с 7:00 до 17:00 по московскому времени.
          |
          |укажите ваш номер телефона""".stripMargin

      Some(
        Reply(
          OutgoingViberMessage(
            UserReplyCommon(
              receive.sender.id,
              receive.sender.api_version,
              reply
            )
          ),
          PhoneCollectionState()
        )
      )
    }.toOption.flatten
  }
}

object InitState {
  def apply(): InitState = new InitState
}
