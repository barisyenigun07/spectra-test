package com.spectra.agent.desktop.mq;

import com.rabbitmq.client.Channel;
import com.spectra.commons.dto.JobCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DesktopJobListener {
    @RabbitListener(queues = "jobs.desktop")
    public void onMessage(JobCreatedEvent evt, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag, @Headers Map<String, Object> headers) {

    }
}
