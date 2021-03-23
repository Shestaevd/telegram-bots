package ru.kvp24.stateMachine

trait IncomingMessage[T] {
  val key: String
  val incomingMessage: T
}

trait OutgoingMessage[T] {
  val outgoingMessage: T
}

case class Reply[In <: IncomingMessage[_], Out <: OutgoingMessage[_]](sendMessage: Out, newState: State[In, Out]) {
  def unapply(): (Out, State[In, Out]) = sendMessage -> newState
}

abstract class State[In <: IncomingMessage[_], Out <: OutgoingMessage[_]]() {
  def handle(message: In): Option[Reply[In, Out]]
}