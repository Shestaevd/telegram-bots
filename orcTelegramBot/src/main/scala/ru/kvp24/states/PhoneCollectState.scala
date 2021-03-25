package ru.kvp24.states

import com.typesafe.config.ConfigFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.kvp24.messages.{IncomingUpdate, OutgoingSendMessage}
import ru.kvp24.stateMachine.{Reply, State}
import ru.kvp24.util.Mailer
import ru.kvp24.{CacheStore, stateMachine}

import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters.SeqHasAsJava

class PhoneCollectState extends State[IncomingUpdate, OutgoingSendMessage] {

  val email: String = ConfigFactory.load().getConfig("orc").getString("email")

  override def handle(incomingUpdate: IncomingUpdate): Option[Reply[IncomingUpdate, OutgoingSendMessage]] = {

    val sendMessage = new SendMessage()
    val update = incomingUpdate.incomingMessage

    Option(update.getMessage).flatMap(msg => Option(msg.getText)) match {
      case Some(number) if number.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$") =>
        val reply = """Укажите ваш email"""

        sendMessage.setText(reply)

        val markup = new InlineKeyboardMarkup()
        val inlineKeyboardButton = new InlineKeyboardButton
        inlineKeyboardButton.setText("Не хочу отправлять email")
        inlineKeyboardButton.setCallbackData("email_refused")
        markup.setKeyboard(List(List(inlineKeyboardButton).asJava).asJava)
        sendMessage.setReplyMarkup(markup)

        CacheStore.scheduler.add(incomingUpdate.key, 15.minutes)(
          Mailer.sendEmail(
            email,
            "Обращение из Telegram bot",
            s"""ФИО - ${update.getMessage.getFrom.getLastName} ${update.getMessage.getFrom.getFirstName}
               |Телефон - $number
               |""".stripMargin
          )
        )

        Some(Reply(OutgoingSendMessage(sendMessage), EmailCollectState(number)))

      case Some(_) =>
        val reply =
          """Формат указанного телефона неверен.
            |Пожалуйста укажите ваш номер телефона, чтобы мы могли связаться с вами.
            |Пример: "+7 код города телефон" или "8 код города номер телефона".
            |""".stripMargin

        sendMessage.setText(reply)
        Some(stateMachine.Reply(OutgoingSendMessage(sendMessage), PhoneCollectState()))
      case _ => None
    }
  }
}

object PhoneCollectState {
  def apply(): PhoneCollectState = new PhoneCollectState
}
