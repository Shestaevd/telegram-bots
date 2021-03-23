package commands

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

trait ReplyCommand extends Command {
  def command: String
  override def handle(message: Message): SendMessage
}
