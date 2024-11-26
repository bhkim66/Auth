package com.bhkim.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MailConfig {
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_DEBUG = "mail.smtp.debug";
    private static final String MAIL_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private final MailProperties mailProperties;
    private final Map<String, String> propertyMap;

    public MailConfig(MailProperties mailProperties, Map<String, String> propertyMap) {
        this.mailProperties = mailProperties;
        this.propertyMap = propertyMap;
    }

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailProperties.getHost());
        javaMailSender.setUsername(mailProperties.getUsername());
        javaMailSender.setPassword(mailProperties.getPassword());
        javaMailSender.setPort(mailProperties.getPort());

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put(MAIL_SMTP_AUTH, propertyMap.get(MAIL_SMTP_AUTH));
        properties.put(MAIL_DEBUG, propertyMap.get(MAIL_DEBUG));
        properties.put(MAIL_CONNECTION_TIMEOUT, propertyMap.get(MAIL_CONNECTION_TIMEOUT));
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, propertyMap.get(MAIL_SMTP_STARTTLS_ENABLE));

        javaMailSender.setJavaMailProperties(properties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}
