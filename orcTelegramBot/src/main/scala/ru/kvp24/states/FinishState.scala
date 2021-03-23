package ru.kvp24.states
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kvp24.messages.{IncomingUpdate, OutgoingSendMessage}
import ru.kvp24.stateMachine
import ru.kvp24.stateMachine.{Reply, State}

class FinishState extends State[IncomingUpdate, OutgoingSendMessage] {

  override def handle(incomingUpdate: IncomingUpdate): Option[Reply[IncomingUpdate, OutgoingSendMessage]] = {
    val update = incomingUpdate.incomingMessage

    Option(update.getMessage) map { _ =>
      val reply = "Мы уже приняли ваше обращение и ответим на него в рабочее время"

      val sendMessage = new SendMessage()
      sendMessage.setText(reply)

      stateMachine.Reply(OutgoingSendMessage(sendMessage), FinishState())
    }
  }
}

object FinishState {
  def apply(): FinishState = new FinishState
}
