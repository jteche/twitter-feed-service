package com.kafka.twitter.exception;

public class CacheOperationException extends RuntimeException {

    public CacheOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
