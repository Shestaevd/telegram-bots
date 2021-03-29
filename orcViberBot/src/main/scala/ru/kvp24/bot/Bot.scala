package ru.kvp24.bot

import akka.http.scaladsl.Http.ServerBinding
import ru.kvp24.http.{Receive, UnCompleteServer, UserReply, UserReplyCommon}
import ru.kvp24.messages._
import ru.kvp24.stateMachine.StateManager
import ru.kvp24.states.InitState

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class Bot(server: UnCompleteServer) {

  val stateManager = new StateManager(8.hours, InitState())

  def create(): (ru.kvp24.http.Server, Future[ServerBinding]) = {
    server.unCompleteServer(List("index" -> handle))
  }

  def handle(receive: Receive): UserReply =
    stateManager
      .messageReceive(receive.sender.id -> receive)
      .fold[UserReply](UserReplyCommon(receive.chat_hostname, receive.sender.api_version, "Произошла обибка на сервере"))(_.outgoingMessage)
}

object Bot {
  def apply(unCompleteServer: UnCompleteServer): (ru.kvp24.http.Server, Future[ServerBinding]) =
    new Bot(unCompleteServer).create()
}

