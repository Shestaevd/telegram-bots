package ru.kvp24.states

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kvp24.messages.{IncomingUpdate, OutgoingSendMessage}
import ru.kvp24.stateMachine
import ru.kvp24.stateMachine.{Reply, State}

class InitState extends State[IncomingUpdate, OutgoingSendMessage] {

  override def handle(incomingUpdate: IncomingUpdate): Option[Reply[IncomingUpdate, OutgoingSendMessage]] = {
    Option(incomingUpdate.incomingMessage.getMessage)
      .flatMap(m => Option(m.getText))
      .map { _ =>

        val reply =
          """Здравствуйте! Вас приветствует виртуальный помощник. Сейчас наша команда отдыхает, но я передам ваше обращение в ближайший рабочий день и с вами свяжутся.
            |В будние дни с 7:00 до 17:00 (МСК) вы можете связаться со специалистами по телефону 8(800)333-23-40 или электронной почте: client@kvp24.ru
            |
            |На какой номер вам позвонить?""".stripMargin

        val sendMessage = new SendMessage()
        sendMessage.setText(reply)

        stateMachine.Reply(OutgoingSendMessage(sendMessage), PhoneCollectState())
      }
  }
}

object InitState {
  def apply(): InitState = new InitState
}
