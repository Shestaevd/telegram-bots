package commands

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

case class StartCommand() extends ReplyCommand() {

  override def handle(message: Message): SendMessage = {
    val reply = new SendMessage()
    reply.setChatId(message.getChatId.toString)
    reply.setText("""Здравствуйте, Вас приветствует Квартплата 24.
                    |Для связи с вами нам нужны ваши контактные данные, оставьте их в ответ на это сообщение.
                    |Обычно мы отвечаем в течение 10-30 минут.
                    |Если вы не видите ответа в течение этого времени, значит вы написали в выходной или нерабочее время.
                    |Мы ответим в первый же рабочий день. Спасибо за обращение.""".stripMargin)
    reply
  }

  override def command: String = "/start"

}
