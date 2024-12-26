package org.example.strategies;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimitStrategy implements RateLimitStrategy {
    private final int maxTokens;
    private final long refillIntervalMillis;
    private final ConcurrentHashMap<String, Bucket> userBuckets;

    public TokenBucketRateLimitStrategy(int maxTokens, long refillIntervalMillis) {
        this.maxTokens = maxTokens;
        this.refillIntervalMillis = refillIntervalMillis;
        this.userBuckets = new ConcurrentHashMap<>();
    }

    private class Bucket {
        private AtomicLong tokens;
        private AtomicLong lastRefillTime;

        public Bucket() {
            this.tokens = new AtomicLong(maxTokens);
            this.lastRefillTime = new AtomicLong(System.currentTimeMillis());
        }

        public boolean allowRequest() {
            refill();
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long refillTokens = (now - lastRefillTime.get()) / refillIntervalMillis;
            if (refillTokens > 0) {
                tokens.set(Math.min(maxTokens, tokens.get() + refillTokens));
                lastRefillTime.set(now);
            }
        }
    }

    @Override
    public boolean allowRequest(String userId) {
        userBuckets.putIfAbsent(userId, new Bucket());
        return userBuckets.get(userId).allowRequest();
    }
}