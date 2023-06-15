package ru.vladislemon.yggdrasilserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class YggdrasilServerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(YggdrasilServerApplication.class, args);
    }

    @Bean
    public JavaMailSender getJavaMailSender(
            @Value("${mail.host}") final String mailHost,
            @Value("${mail.port}") final int mailPort,
            @Value("${mail.username}") final String mailUsername,
            @Value("${mail.password}") final String mailPassword
    ) {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        final Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
