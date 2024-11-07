package com.utng.integradora.email;

import java.io.File;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Multipart;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailHelperImpl implements MailHelper{
    @Autowired
    private JavaMailSender javaMailSender;
    public boolean send(String to, String from, String subject, String text, String cco, File... files){
        to = to.replace(";", ",");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            if(cco !=null){
                helper.setBcc(cco.split(","));
            }
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(text, "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            if(files != null){
                for(File file : files){
                    bodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    bodyPart.setDataHandler(new DataHandler(source));
                    multipart.addBodyPart(bodyPart);
                }
            }
            mimeMessage.setReplyTo(new jakarta.mail.Address[]{
                    new jakarta.mail.internet.InternetAddress("fernanda31010281@gmail.com")
            });
            mimeMessage.setContent(multipart);
            String[] to1 = to.split(",");
            helper.setTo(to1);
            helper.setSubject(subject);
            javaMailSender.send(mimeMessage);
            return true;
        } catch(MessagingException e){
            System.out.println("MessagingException"+e);
            return false;
        }
    }

    @Override
    public boolean send(String to, String from, String subject, String text, String cco){
            return send(to, from, subject, text, cco, null);
    }

    @Override
    public boolean send(String to, String from, String subject, String text){
        return send(to, from, subject, text, null);
    }
}
