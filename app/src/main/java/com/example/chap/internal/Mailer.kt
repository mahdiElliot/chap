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
        colorful: Boolean,
        phone: String,
        address: String,
        time: String? = "",
        fileName: String? = "",
        fileName2: String? = "",
        description: String? = ""
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

                    mime.subject = "خدمات کپی و تکثیر یا چاپ"

                    var text = "$description\n\n"
                    text += "نوع چاپ: "
                    text += if (colorful) "رنگی\n" else "سیاه و سفید\n"
                    text += "شماره تلفن: $phone\n"
                    text += "آدرس:  $address\n"
                    if (time != "")
                        text += "زمان: $time"

                    val multipart = MimeMultipart()

                    val message = MimeBodyPart()
                    message.setText(text)
                    multipart.addBodyPart(message)

                    if (fileName != "" || !fileName.isNullOrEmpty()) {
                        val messageBodyPart = MimeBodyPart()
                        val source = FileDataSource(File(fileName!!))
                        messageBodyPart.dataHandler = DataHandler(source)
                        messageBodyPart.fileName = fileName
                        multipart.addBodyPart(messageBodyPart)
                    }

                    if (fileName2 != "" || !fileName2.isNullOrEmpty()) {
                        val messageBodyPart = MimeBodyPart()
                        val source = FileDataSource(File(fileName2!!))
                        messageBodyPart.dataHandler = DataHandler(source)
                        messageBodyPart.fileName = fileName2
                        multipart.addBodyPart(messageBodyPart)
                    }

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