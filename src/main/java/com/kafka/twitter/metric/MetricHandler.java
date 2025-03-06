package com.kafka.twitter.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;

@Component
public class MetricHandler {

    public static final Counter NUMBER_OF_TWITTER_MESSAGES = Counter.builder("number_of_tweets")
            .description("The number of tweeter messages")
            .register(Metrics.globalRegistry);


    public static final Counter NUMBER_OF_FAILED_TWITTER_MESSAGES = Counter.builder("error_counts")
            .description("The number of error occurs while processing the message")
            .register(Metrics.globalRegistry);
}