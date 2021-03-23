package ru.kvp24.states

import com.typesafe.config.ConfigFactory
import ru.kvp24.CacheStore
import ru.kvp24.http.{
  Button,
  Keyboard,
  UserReply,
  UserReplyCommon,
  UserReplyWithKeyboard
}
import ru.kvp24.messages.{IncomingViberMessage, OutgoingViberMessage}
import ru.kvp24.stateMachine.{Reply, State}
import ru.kvp24.util.Mailer

import scala.concurrent.duration.DurationInt
import scala.util.Try

class PhoneCollectionState
    extends State[IncomingViberMessage, OutgoingViberMessage] {

  val email: String = ConfigFactory.load().getConfig("orc").getString("email")

  override def handle(
      message: IncomingViberMessage
  ): Option[Reply[IncomingViberMessage, OutgoingViberMessage]] = {
    Try[Option[Reply[IncomingViberMessage, OutgoingViberMessage]]] {
      val receive = message.incomingMessage

      if (
        receive.message.text.matches(
          "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$"
        )
      ) {

        val reply = """Укажите ваш email"""

        Button(ActionBody = "email_refuse", Text = "Не хочу отправлять email")
        val keyboard = Keyboard(Buttons =
          (Button(
            ActionBody = "email_refuse",
            Text = "Не хочу отправлять email"
          ) :: Nil).toArray
        )

        CacheStore.scheduler.add(message.key, 15.minutes)(
          Mailer.sendEmail(
            email,
            "Обращение из Viber bot",
            s"""ФИО - ${receive.sender.name}
             |Телефон - ${receive.message.text}
             |""".stripMargin
          )
        )

        Some(
          Reply(
            OutgoingViberMessage(
              UserReplyWithKeyboard(
                receive.sender.id,
                receive.sender.api_version,
                reply,
                keyboard = keyboard
              )
            ),
            EmailCollectionState(receive.message.text)
          )
        )
      } else {
        val reply =
          """Формат указанного телефона неверен.
          |Пожалуйста укажите ваш номер телефона, чтобы мы могли связаться с вами.
          |Пример: "+7 код города телефон" или "8 код города номер телефона".
          |""".stripMargin

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
      }
    }
  }.toOption.flatten
}

object PhoneCollectionState {

  def apply(): PhoneCollectionState = new PhoneCollectionState

}
