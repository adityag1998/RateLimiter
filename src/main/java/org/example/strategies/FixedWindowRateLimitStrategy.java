package org.example.strategies;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowRateLimitStrategy implements RateLimitStrategy {
    private final int maxRequests;
    private final long windowMillis;
    private final ConcurrentHashMap<String, Window> userWindows;

    public FixedWindowRateLimitStrategy (int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.userWindows = new ConcurrentHashMap<>();
    }

    private class Window {
        private AtomicInteger requestCount;
        private AtomicLong startTime;

        public Window() {
            this.requestCount = new AtomicInteger(0);
            this.startTime = new AtomicLong(System.currentTimeMillis());
        }

        public boolean allowRequest() {
            AtomicLong now = new AtomicLong(System.currentTimeMillis());
            if (now.addAndGet(-startTime.get()) >= windowMillis) {
                startTime = now;
                requestCount = new AtomicInteger(0);
            }
            if (requestCount.get() < maxRequests) {
                requestCount.incrementAndGet();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean allowRequest(String userId) {
        userWindows.putIfAbsent(userId, new Window());
        return userWindows.get(userId).allowRequest();
    }
}
