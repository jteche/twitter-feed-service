package com.kafka.twitter.service;

import com.kafka.twitter.exception.CacheOperationException;
import com.kafka.twitter.model.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class TwitterCacheService {

    private static final Logger logger = LoggerFactory.getLogger(TwitterCacheService.class);
    private static final String TWEET_KEY_PREFIX = "tweet:";
    private static final Duration CACHE_DURATION = Duration.ofDays(7);

    private final RedisTemplate<String, Object> redisTemplate;
    private final RetryTemplate retryTemplate;

    public TwitterCacheService(RedisTemplate<String, Object> redisTemplate, RetryTemplate retryTemplate) {
        this.redisTemplate = redisTemplate;
        this.retryTemplate = retryTemplate;
    }

    /**
     * Generic method to execute Redis operations with retry and exception handling
     *
     * @param operation The operation to execute
     * @param errorMessage Error message for logging
     * @param <T> Return type of the operation
     * @return Result of the operation
     */
    private <T> T executeWithRetry(Supplier<T> operation, String errorMessage) {
        try {
            return retryTemplate.execute(context -> operation.get());
        } catch (Exception e) {
            logger.error(errorMessage, e);
            throw new CacheOperationException(errorMessage, e);
        }
    }

    /**
     * Overloaded method for void operations
     */
    private void executeWithRetry(Runnable operation, String errorMessage) {
        executeWithRetry(() -> {
            operation.run();
            return null;
        }, errorMessage);
    }

    public void cacheTweet(RawMessage tweet) {
        String key = TWEET_KEY_PREFIX + tweet.getId();
        executeWithRetry(
            () -> redisTemplate.opsForValue().set(key, tweet, CACHE_DURATION),
            String.format("Failed to cache tweet with id: %s", tweet.getId())
        );
    }

    public RawMessage getTweet(String tweetId) {
        String key = TWEET_KEY_PREFIX + tweetId;
        return executeWithRetry(
            () -> {
                Object tweet = redisTemplate.opsForValue().get(key);
                if (tweet == null) {
                    logger.debug("Cache miss for tweet id: {}", tweetId);
                    return null;
                }
                return (RawMessage) tweet;
            },
            String.format("Failed to retrieve tweet with id: %s", tweetId)
        );
    }

    public void deleteTweet(String tweetId) {
        String key = TWEET_KEY_PREFIX + tweetId;
        executeWithRetry(
            () -> redisTemplate.delete(key),
            String.format("Failed to delete tweet with id: %s", tweetId)
        );
    }

    public void clearExpiredTweets() {
        executeWithRetry(
            () -> {
                redisTemplate.keys(TWEET_KEY_PREFIX + "*").forEach(key -> {
                    Long ttl = redisTemplate.getExpire(key, TimeUnit.DAYS);
                    if (ttl != null && ttl <= 0) {
                        redisTemplate.delete(key);
                        logger.info("Deleted expired tweet with key: {}", key);
                    }
                });
            },
            "Failed to clear expired tweets"
        );
    }
}
