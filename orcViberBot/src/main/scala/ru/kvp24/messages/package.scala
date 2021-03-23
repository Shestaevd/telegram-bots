package ru.kvp24

import ru.kvp24.http.{Receive, UserReply}
import ru.kvp24.stateMachine.{IncomingMessage, OutgoingMessage}

package object messages {

  case class IncomingViberMessage(override val key: String,  override val incomingMessage: Receive) extends IncomingMessage[Receive]

  case class OutgoingViberMessage(override val outgoingMessage: UserReply) extends OutgoingMessage[UserReply]

  implicit def in(tuple: (String, Receive)): IncomingViberMessage = IncomingViberMessage(tuple._1, tuple._2)

}
