package com.findataagg.notificationservice.events;

import com.findataagg.common.messaging.model.AlertTriggeredEvent;
import com.findataagg.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertListenerService {

    private final EmailService emailService;

    public AlertListenerService(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${app.rabbitmq.alerts-queue-name}")
    public void handleAlertTriggeredEvent(AlertTriggeredEvent event) {
        log.info("Received ALERT TRIGGERED event: {}", event);
        // For now, we only have one notification channel: email
        emailService.sendAlertEmail(event);
    }

}
