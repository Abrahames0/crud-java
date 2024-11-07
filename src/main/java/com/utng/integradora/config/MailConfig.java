package com.utng.integradora.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender inicializaMail(){
        String emailPass="fernanda31010281@gmail.com;zfes nuwf ccls slhr";
        String emailSmtp="smtp.gmail.com;587";
        String mailDebug="false";


        JavaMailSenderImpl avaMailSender = new JavaMailSenderImpl();
        String[] smtp = emailSmtp.split(";");
        avaMailSender.setHost(smtp[0]);
        avaMailSender.setPort(Integer.valueOf(smtp[1]));
        String[] email = emailPass.split(";");
        avaMailSender.setUsername(email[0]);
        avaMailSender.setPassword(email[1]);
        Properties props = avaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", mailDebug);
        props.put("mail.smtp.user", email[0]);
        props.put("mail.smtp.port", Integer.valueOf(smtp[1]));
        props.put("mail.host", smtp[0]);
        avaMailSender.setJavaMailProperties(props);
        return avaMailSender;
    }
}
