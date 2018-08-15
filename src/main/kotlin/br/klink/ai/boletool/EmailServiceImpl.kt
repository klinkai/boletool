package br.klink.ai.boletool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.io.File

@Component
class EmailServiceImpl {

    @Autowired
    var emailSender: JavaMailSender? = null

    fun sendSimpleMessage(
            to: String, subject: String, text: String, boleto : File, fileName : String) {

        val message = emailSender!!.createMimeMessage()

        val helper = MimeMessageHelper(message, true)

        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(text)

        val file = FileSystemResource(boleto)
        helper.addAttachment(fileName, file)

        emailSender!!.send(message)

    }
}