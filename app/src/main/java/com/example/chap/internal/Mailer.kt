package com.example.chap.internal

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import io.reactivex.Completable
import java.io.*
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

object Mailer {
    @SuppressLint("checkResult")
    fun sendMail(
        email: String,
        subject: String,
        colorful: Boolean,
        phone: String,
        address: String,
        fileName: String,
        fileName2: String?,
        description: String?
    ): Completable {
        return Completable.create { emitter ->

            val props: Properties = Properties().also {
                it.put("mail.smtp.host", "smtp.gmail.com")
                it.put("mail.smtp.socketFactory.port", "465")
                it.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                it.put("mail.smtp.auth", "true")
                it.put("mail.smtp.port", "465")
            }

            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Config.EMAIL, Config.PASSWORD)
                }
            })

            try {
                MimeMessage(session).let { mime ->
                    mime.setFrom(InternetAddress(Config.EMAIL))

                    mime.addRecipient(Message.RecipientType.TO, InternetAddress(email))

                    mime.subject = subject
                    var text = "$description\n\n"
                    text += "نوع چاپ:"
                    text += if (colorful) text + "رنگی\n" else text + "سیاه و سفید\n"
                    text += "شماره تلفن: $phone\n"
                    text += "آدرس:  $address\n"
                    mime.setText(description)

                    val messageBodyPart = MimeBodyPart()

                    val path = File(fileName)
                    val source = FileDataSource(path)
                    messageBodyPart.dataHandler = DataHandler(source)
                    messageBodyPart.fileName = fileName
                    val multipart = MimeMultipart()
                    multipart.addBodyPart(messageBodyPart)
                    mime.setContent(multipart)
                    Transport.send(mime)
                }
            } catch (e: MessagingException) {
                emitter.onError(e)
                Log.i("whynot", e.message!!)
            }

            emitter.onComplete()
        }
    }

}