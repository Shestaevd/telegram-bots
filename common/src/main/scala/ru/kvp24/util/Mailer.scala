package ru.kvp24.util

import com.typesafe.config.{Config, ConfigFactory}

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Authenticator, Message, PasswordAuthentication, Session, Transport}

object Mailer {

  private val emailConf: Config = ConfigFactory.load().getConfig("emailConf")
  private val from: String = emailConf.getString("from")
  private val pass: String = emailConf.getString("password")
  private val port: String = emailConf.getString("port")

  def sendEmail(to: String, header: String, text: String): Unit = {
    val props = new Properties()
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.smtp.host", "smtp.gmail.com")
    props.put("mail.smtp.port", port)

    val session: Session = Session.getInstance(props, new Authenticator {
      override def getPasswordAuthentication: PasswordAuthentication = {
        new PasswordAuthentication(from, pass)
      }
    })

    val message = createMessage(to, from, header, text)(session)
    Transport.send(message)
  }

  private def createMessage(to: String, from: String, header: String, text: String)(session: Session): Message = {
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to))
    message.setSubject(header)
    message.setText(text, "UTF-8")
    message
  }

}
