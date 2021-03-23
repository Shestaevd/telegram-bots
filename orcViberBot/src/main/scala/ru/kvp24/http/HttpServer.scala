package ru.kvp24.http

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling.Unmarshal
import ru.kvp24.util.AkkaSystem._
import spray.json.{DefaultJsonProtocol, RootJsonFormat, enrichAny}

import scala.concurrent.Future

trait Marshaller extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val senderFormat: RootJsonFormat[Sender] = jsonFormat5(Sender)
  implicit val messageFormat: RootJsonFormat[Message] = jsonFormat2(Message)
  implicit val orderFormat: RootJsonFormat[Receive] = jsonFormat7(Receive)
  implicit val userReplyFormat: RootJsonFormat[UserReplyCommon] = jsonFormat4(UserReplyCommon)
  implicit val buttonFormat: RootJsonFormat[Button] = jsonFormat4(Button)
  implicit val keyboardFormat: RootJsonFormat[Keyboard] = jsonFormat3(Keyboard)
  implicit val userReplyWithKeyboardFormat: RootJsonFormat[UserReplyWithKeyboard] = jsonFormat5(UserReplyWithKeyboard)
}

case class UnCompleteServer(
    unCompleteServer: List[(String, Receive => UserReply)] => (Server, Future[ServerBinding])
)

class Server(interface: String, port: Int, token: String)(
    receiveHandlers: Map[String, Receive => UserReply]
) extends Directives
    with Marshaller {

  def create(): Future[ServerBinding] = {
    val route =
      concat(
        post {
          path("index") {
            entity(as[Receive]) { receive =>

            println(receive)

              receiveHandlers
                .get("index")
                .map(_(receive))
                .foreach(sendReplyAkka)
              complete(StatusCodes.OK)
            }
          }
        }
      )

    Http().newServerAt(interface, port).bind(route)
  }

  def sendReplyAkka(reply: UserReply): Unit = {

    val json = reply match {
      case reply: UserReplyCommon       => reply.toJson
      case reply: UserReplyWithKeyboard => reply.toJson
      case error => throw new Exception(s"unable to parse ${error.getClass} to json")
    }

    println(json.toString())

    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "https://chatapi.viber.com/pa/send_message",
        entity = HttpEntity(
          ContentType(MediaTypes.`application/json`),
          json.toString().getBytes("UTF-8")
        )
      ).withHeaders(
        RawHeader("x-viber-auth-token", token),
        RawHeader("charset", "utf-8")
      )
    ).onComplete(ok => println(Unmarshal(ok.get.entity).to[String]))
  }

}

object Server {

  /** Создает новый http сервер.
    * @return функцию которая ожидает список хендлеров
    */
  def apply(interface: String, port: Int, token: String): UnCompleteServer =
    UnCompleteServer(unCompleteServer =>
      Option(new Server(interface: String, port: Int, token: String)(unCompleteServer.toMap))
        .map(server => server -> server.create())
        .get
    )
}
