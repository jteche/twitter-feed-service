package com.kafka.twitter.service;

import com.kafka.twitter.metric.MetricHandler;
import com.kafka.twitter.model.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/api")
public class TwitterApiRestController {

    private static final Logger logger = LoggerFactory.getLogger(TwitterApiRestController.class);

    private final TwitterFeedKafkaProducer twitterFeedKafkaProducer;
    private final MetricHandler metricHandler;

    public TwitterApiRestController(TwitterFeedKafkaProducer twitterFeedKafkaProducer, MetricHandler metricHandler) {
        this.twitterFeedKafkaProducer = twitterFeedKafkaProducer;
        this.metricHandler = metricHandler;
    }

    @PostMapping(value = "/v1/publish")
    public void produceTwitterData(@RequestBody RawMessage message) {
        publishMessage(message);
    }

    @PostMapping(value = "/v2/publish")
    public void produceTwitterDataWithSenderDetail(@RequestBody RawMessage message) {
        publishMessage(message);
    }

    private void publishMessage(RawMessage message) {
        try {
            this.twitterFeedKafkaProducer.sendMessage(message);
            MetricHandler.NUMBER_OF_TWITTER_MESSAGES.increment();
        } catch (Exception e) {
            MetricHandler.NUMBER_OF_FAILED_TWITTER_MESSAGES.increment();
            logger.error("Failed to publish message: {}", e.getMessage());
            throw e;
        }
    }
}
