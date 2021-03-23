package ru.kvp24.messages

import org.telegram.telegrambots.meta.api.objects.Update
import ru.kvp24.stateMachine.IncomingMessage

case class IncomingUpdate(override val key: String, override val incomingMessage: Update) extends IncomingMessage[Update]