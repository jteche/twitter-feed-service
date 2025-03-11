package com.kafka.twitter.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.RedisConnection;


@Component
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;
    public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }



    @Override
    public Health health() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pong = new String(connection.ping());
            if ("PONG".equals(pong)) {
                return Health.up()
                        .withDetail("version", connection.info().getProperty("redis_version"))
                        .withDetail("mode", connection.info().getProperty("redis_mode"))
                        .build();
            }
            return Health.down().withDetail("ping", "no response").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
