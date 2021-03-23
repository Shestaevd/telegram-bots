package ru.kvp24.util

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import com.typesafe.config.ConfigFactory
import ru.kvp24.util.AkkaSystem._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Logger {

  private val loggerGroupId: String = ConfigFactory.load().getConfig("group").getString("loggerGroup")
  private val token: String = ConfigFactory.load().getConfig("bot-logger").getString("token")

  private def sendMessage(message: String): Unit = {
    val toSend = validateString(message)
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = s"https://api.telegram.org/bot$token/sendMessage?chat_id=$loggerGroupId&text=$toSend")
    )
  }

  private def validateString(str: String): String = {
    str.replace(' ', '+')
  }

  def info(message: String): Unit = {
    sendMessage(s"INFO - $message")
  }

  def error(message: String): Unit = {
    sendMessage(s"ERROR - $message")
  }

  def warning(message: String): Unit = {
    sendMessage(s"WARNING - $message")
  }

  def info(message: String, botName: String): Unit = {
    sendMessage(s"INFO:$botName - $message")
  }

  def error(message: String, botName: String): Unit = {
    sendMessage(s"ERROR:$botName - $message")
  }

  def warning(message: String, botName: String): Unit = {
    sendMessage(s"WARNING:$botName - $message")
  }
}
