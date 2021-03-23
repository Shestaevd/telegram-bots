package ru.kvp24.util

import akka.actor.{ActorSystem, Cancellable}
import com.github.blemale.scaffeine.{Cache, Scaffeine}

import scala.concurrent.duration._

object Scheduler {
  def apply(finiteDuration: FiniteDuration): Scheduler = new Scheduler(finiteDuration: FiniteDuration)
}

class Scheduler(finiteDuration: FiniteDuration) {

  private val schedulerCache: Cache[String, Cancellable] = Scaffeine()
    .recordStats()
    .expireAfterWrite(finiteDuration)
    .build[String, Cancellable]()

  def add(uuid: String, scheduleDuration: FiniteDuration)(method: => Unit): Unit = {
    if (scheduleDuration > finiteDuration) throw new Exception("Cache duration is less then schedule duration ")
    schedulerCache
      .put(uuid, AkkaSystem.system.scheduler
        .scheduleOnce(scheduleDuration)(method)(AkkaSystem.system.dispatcher))
  }

  def drop(uuid: String): Unit = schedulerCache.getIfPresent(uuid).foreach { method =>
    method.cancel()
    schedulerCache.invalidate(uuid)
  }

  def update(uuid: String, scheduleDuration: FiniteDuration)(method: => Unit): Unit = {
    drop(uuid)
    add(uuid, scheduleDuration)(method)
  }
}
