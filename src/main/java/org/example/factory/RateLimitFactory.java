package org.example.factory;

import org.example.enums.RateLimitAlgorithmType;
import org.example.strategies.*;

public class RateLimitFactory {
    public static RateLimitStrategy getRateLimitStrategy (RateLimitAlgorithmType type, int param1, int param2) {
        switch (type) {
            case FIXED_WINDOW:
                return new FixedWindowRateLimitStrategy(param1, param2);
            case SLIDING_WINDOW:
                return new SlidingWindowRateLimitStrategy(param1, param2);
            case TOKEN_BUCKET:
                return new TokenBucketRateLimitStrategy(param1, param2);
            case LEAKY_BUCKET:
                return new LeakyBucketRateLimitStrategy(param1, param2);
            default:
                throw new IllegalArgumentException("Invalid rate limit algorithm type");
        }
    }
}
