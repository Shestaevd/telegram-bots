package ru.kvp24.messages

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import ru.kvp24.stateMachine.OutgoingMessage

case class OutgoingSendMessage(override val outgoingMessage: SendMessage) extends OutgoingMessage[SendMessage]()
