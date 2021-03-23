package ru.kvp24.http

case class Receive(
    event: String,
    timestamp: Long,
    chat_hostname: String,
    message_token: Long,
    sender: Sender,
    message: Message,
    silent: Boolean
)

case class Sender(
    id: String,
    name: String,
    language: String,
    country: String,
    api_version: Int
)

case class Message(
    text: String,
    `type`: String
)

trait UserReply {
  val receiver: String
  val min_api_version: Int
  val `type`: String = "text"
  val text: String
}

case class Keyboard(
    Type: String = "keyboard",
    DefaultHeight: Boolean = true,
    Buttons: Array[Button]
)

case class Button(
    ActionType: String = "reply",
    ActionBody: String,
    Text: String,
    TextSize: String = "regular"
)

case class UserReplyCommon(
    override val receiver: String,
    override val min_api_version: Int,
    override val text: String,
    override val `type`: String = "text"
) extends UserReply

case class UserReplyWithKeyboard(
    override val receiver: String,
    override val min_api_version: Int,
    override val text: String,
    override val `type`: String = "text",
    keyboard: Keyboard
) extends UserReply
