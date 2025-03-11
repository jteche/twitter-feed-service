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
    private final KafkaTemplate<String, RawMessage> kafkaTemplate;
    private final MetricHandler metricHandler;
    private final TwitterCacheService twitterCacheService;

    @Autowired
    public TwitterFeedKafkaProducer(KafkaTemplate<String, RawMessage> kafkaTemplate, MetricHandler metricHandler, TwitterCacheService twitterCacheService) {
        this.kafkaTemplate = kafkaTemplate;
        this.metricHandler = metricHandler;
        this.twitterCacheService = twitterCacheService;
    }



    public void sendMessage(RawMessage message) {
        // send message to kafka
        this.kafkaTemplate.send(TOPIC, message);
        LOGGER.info("#### -> Producing message -> {}", message);
        LOGGER.info("#### -> Total Twitter counts : {}", MetricHandler.NUMBER_OF_TWITTER_MESSAGES.count());

        // cache the message
        twitterCacheService.cacheTweet(message);
    }

}
