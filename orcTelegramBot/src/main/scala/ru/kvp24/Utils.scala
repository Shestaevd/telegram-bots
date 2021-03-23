package ru.kvp24

import org.telegram.telegrambots.meta.api.objects.Update

object Utils {

  def extractChatId(update: Update): Option[String] =
    Option(update.getMessage).orElse(Option(update.getCallbackQuery.getMessage)).map(_.getChatId.toString)

}