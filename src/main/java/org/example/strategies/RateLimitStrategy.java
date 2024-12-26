package org.example.strategies;

public interface RateLimitStrategy {
    boolean allowRequest (String pivotId);
}
