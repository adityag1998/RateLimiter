package org.example.services;

import org.example.enums.RateLimitAlgorithmType;
import org.example.factory.RateLimitFactory;
import org.example.strategies.RateLimitStrategy;

public class RateLimitService {
    private static RateLimitService instance;
    private final RateLimitStrategy rateLimiter;

    private RateLimitService() {
        rateLimiter = RateLimitFactory.getRateLimitStrategy(RateLimitAlgorithmType.LEAKY_BUCKET, 10, 1000);
    }

    public static RateLimitService getInstance() {
        if (instance == null) {
            synchronized (RateLimitService.class) {
                if (instance == null) {
                    instance = new RateLimitService();
                }
            }
        }
        return instance;
    }

    public boolean allowRequest(String pivotId) {
        return rateLimiter.allowRequest(pivotId);
    }
}
