package ru.kvp24.states

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kvp24.messages.{IncomingUpdate, OutgoingSendMessage}
import ru.kvp24.stateMachine
import ru.kvp24.stateMachine.{Reply, State}

class InitState extends State[IncomingUpdate, OutgoingSendMessage] {

  override def handle(incomingUpdate: IncomingUpdate): Option[Reply[IncomingUpdate, OutgoingSendMessage]] = {
    Option(incomingUpdate.incomingMessage.getMessage)
      .map(_.getText)
      .map { _ =>

        val reply =
          """Уважаемый посетитель, к сожалению, вы написали нам в не рабочее время.
                        |Вы можете отправить запрос на нашу электронную почту client@kvp24.ru или позвонить нам в рабочее время по бесплатному номеру 8-800-333-23-40
                        |Мы работаем с понедельника по пятницу с 7:00 до 17:00 по московскому времени.
                        |
                        |укажите ваш номер телефона""".stripMargin

        val sendMessage = new SendMessage()
        sendMessage.setText(reply)

        stateMachine.Reply(OutgoingSendMessage(sendMessage), PhoneCollectState())
      }
  }
}

object InitState {
  def apply(): InitState = new InitState
}
