package org.example.strategies;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SlidingWindowRateLimitStrategy implements RateLimitStrategy {

    private final Map<String, ConcurrentHashMap<Long, AtomicInteger>> userToBucketMap;
    private final int maxRequests;
    private final long windowMillis;

    public SlidingWindowRateLimitStrategy(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.userToBucketMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String userId) {
        long currentSecond = System.currentTimeMillis() / 1000; // Time in seconds
        userToBucketMap.putIfAbsent(userId, new ConcurrentHashMap<>());
        ConcurrentHashMap<Long, AtomicInteger> bucketMap = userToBucketMap.get(userId);

        // Increment the current second's bucket
        AtomicInteger requestCount = bucketMap.computeIfAbsent(currentSecond, k -> new AtomicInteger(0));
        requestCount.incrementAndGet();

        // Cleanup old buckets
        cleanupOldBuckets(bucketMap, currentSecond);

        // Calculate total requests within the sliding window
        int totalRequests = bucketMap.values().stream().mapToInt(AtomicInteger::get).sum();

        // Check if the total requests exceed the limit
        return totalRequests <= maxRequests;
    }

    private void cleanupOldBuckets(ConcurrentHashMap<Long, AtomicInteger> bucketMap, long currentSecond) {
        long boundary = currentSecond - (windowMillis / 1000); // Convert window size to seconds
        bucketMap.keySet().removeIf(timestamp -> timestamp <= boundary);
    }
}