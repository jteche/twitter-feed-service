package com.kafka.twitter.service;

import com.kafka.twitter.metric.MetricHandler;
import com.kafka.twitter.model.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterFeedKafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterFeedKafkaProducer.class);
    private static final String TOPIC = "twitter-feed-topic";

    @Autowired
    private KafkaTemplate<String, RawMessage> kafkaTemplate;

    public void sendMessage(RawMessage message) {
        this.kafkaTemplate.send(TOPIC, message);
        LOGGER.info("#### -> Producing message -> {}", message);
        LOGGER.info("#### -> Total Twitter counts : {}", MetricHandler.NUMBER_OF_TWITTER_MESSAGES.count());

    }

}
