package com.findataagg.notificationservice.service;

import com.findataagg.common.messaging.model.AlertTriggeredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(AlertTriggeredEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmailAddress); // Set the from address
            message.setTo(event.userEmail());
            message.setSubject(String.format("Stock Alert Triggered for %s!", event.symbol()));

            String text = String.format(
                    """
                            Hello!
                            
                            This is an automated alert from the Financial Data Aggregator.
                            
                            An alert you configured has been triggered:
                            - Symbol: %s
                            - Condition: Price %s %s
                            - Current Price: %s
                            - Notes: %s
                            
                            Thank you,
                            The System
                            """,
                    event.symbol(),
                    event.condition().replace("_", " "),
                    event.triggerValue(),
                    event.actualValue(),
                    event.notes() != null ? event.notes() : "N/A"
            );

            message.setText(text);
            mailSender.send(message);
            log.info("Successfully sent email notification for rule ID: {}", event.ruleId());
        } catch (Exception e) {
            log.error("Failed to send email for alert rule ID: {}", event.ruleId(), e);
        }
    }

}
