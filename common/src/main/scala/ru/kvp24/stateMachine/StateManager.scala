package ru.kvp24.stateMachine

import com.github.blemale.scaffeine.{Cache, Scaffeine}

import scala.concurrent.duration._

class StateManager[In <: IncomingMessage[_], Out <: OutgoingMessage[_]](expire: FiniteDuration, initState: State[In, Out]) {

  private val cache: Cache[String, State[In, Out]] = Scaffeine()
    .recordStats()
    .expireAfterWrite(expire)
    .build[String, State[In, Out]]()

  def messageReceive(update: In): Option[Out] =
    cache
      .asMap()
      .collectFirst { case (chat, state) if chat.equals(update.key) => state }
      .getOrElse(initState)
      .handle(update)
      .map {
        case Reply(sendMessage, state) =>
          cache.put(update.key, state)
          sendMessage
      }
}
