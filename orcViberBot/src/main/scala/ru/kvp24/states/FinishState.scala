package ru.kvp24.states

import ru.kvp24.http.UserReplyCommon
import ru.kvp24.messages.{IncomingViberMessage, OutgoingViberMessage}
import ru.kvp24.stateMachine.{Reply, State}

import scala.util.Try

class FinishState extends State[IncomingViberMessage, OutgoingViberMessage] {
  override def handle(
      message: IncomingViberMessage
  ): Option[Reply[IncomingViberMessage, OutgoingViberMessage]] = {
    Try[Option[Reply[IncomingViberMessage, OutgoingViberMessage]]] {
      val reply =
        "Мы уже приняли ваше обращение и ответим на него в рабочее время"
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

object FinishState {

  def apply(): FinishState = new FinishState

}
