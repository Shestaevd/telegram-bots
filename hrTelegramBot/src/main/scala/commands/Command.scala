package commands

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

trait Command {
  def handle(message: Message): SendMessage
}
