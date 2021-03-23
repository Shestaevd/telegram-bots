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
      .find { case (chat, _) => chat.equals(update.key) }
      .map(_._2)
      .fold(initState.handle(update))(_.handle(update))
      .map {
        case Reply(sendMessage, state) =>
          cache.put(update.key, state)
          sendMessage
      }
}
