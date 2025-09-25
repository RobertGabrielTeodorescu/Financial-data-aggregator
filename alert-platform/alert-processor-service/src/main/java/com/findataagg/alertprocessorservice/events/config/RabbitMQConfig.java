package com.findataagg.alertprocessorservice.events.config;

import com.findataagg.common.messaging.config.BaseRabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.trades-queue-name}")
    private String tradesQueueName;

    @Value("${app.rabbitmq.quotes-queue-name}")
    private String quotesQueueName;

    @Value("${app.rabbitmq.trades-routing-key}")
    private String tradesRoutingKey;

    @Value("${app.rabbitmq.quotes-routing-key}")
    private String quotesRoutingKey;

    @Bean
    public Queue stockPriceQueue() {
        return new Queue(tradesQueueName, true);
    }

    @Bean
    public Queue stockQuoteQueue() {
        return new Queue(quotesQueueName, true);
    }

    @Bean
    public Binding stockPriceBinding(@Qualifier("stockPriceQueue") Queue stockPriceQueue, @Qualifier("priceEventsTopicExchange") TopicExchange priceEventsTopicExchange) {
        return createBindingHelper(stockPriceQueue, priceEventsTopicExchange, tradesRoutingKey);
    }

    @Bean
    public Binding stockQuoteBinding(@Qualifier("stockQuoteQueue") Queue stockQuoteQueue, @Qualifier("priceEventsTopicExchange") TopicExchange priceEventsTopicExchange) {
        return createBindingHelper(stockQuoteQueue, priceEventsTopicExchange, quotesRoutingKey);
    }

    private Binding createBindingHelper(Queue queue, TopicExchange priceEventsTopicExchange, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(priceEventsTopicExchange)
                .with(routingKey);
    }

}
