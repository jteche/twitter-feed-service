package com.kafka.twitter.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/v1/cache")
public class CacheManagementController {

    private final TwitterCacheService cacheService;

    public CacheManagementController(TwitterCacheService twitterCacheService) {
        this.cacheService = twitterCacheService;
    }

    @GetMapping("/tweet/{id}")
    public ResponseEntity<?> getTweet(@PathVariable String id) {
        var tweet = cacheService.getTweet(id);
        return tweet != null ?
                ResponseEntity.ok(tweet) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/tweet/{id}")
    public ResponseEntity<?> deleteTweet(@PathVariable String id) {
        cacheService.deleteTweet(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupExpiredTweets() {
        cacheService.clearExpiredTweets();
        return ResponseEntity.ok().build();
    }
}
