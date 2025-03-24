package com.example.CapstoneFinalProjectBE.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void inviaEmail(String destinatario, String oggetto, String testo) throws MessagingException {
        MimeMessage messaggio = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(messaggio, false, "utf-8");
        helper.setTo(destinatario);
        helper.setSubject(oggetto);
        helper.setText(testo, false); // false = plain text
        mailSender.send(messaggio);
    }

}
