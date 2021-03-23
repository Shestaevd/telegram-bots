package ru.kvp24

import ru.kvp24.util.Scheduler

import scala.concurrent.duration._

object CacheStore {

  val scheduler: Scheduler = Scheduler(15.minutes)

}
