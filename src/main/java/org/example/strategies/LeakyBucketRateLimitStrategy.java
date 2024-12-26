package org.example.strategies;
import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketRateLimitStrategy implements RateLimitStrategy {
    private final int capacity;
    private final long leakRateMillis;
    private final ConcurrentHashMap<String, Bucket> userBuckets;

    public LeakyBucketRateLimitStrategy(int capacity, long leakRateMillis) {
        this.capacity = capacity;
        this.leakRateMillis = leakRateMillis;
        this.userBuckets = new ConcurrentHashMap<>();
    }

    private class Bucket {
        private int waterLevel;
        private long lastLeakTime;

        public Bucket() {
            this.waterLevel = 0;
            this.lastLeakTime = System.currentTimeMillis();
        }

        public synchronized boolean allowRequest() {
            leak();
            if (waterLevel < capacity) {
                waterLevel++;
                return true;
            }
            return false;
        }

        private void leak() {
            long now = System.currentTimeMillis();
            long leakedDrops = (now - lastLeakTime) / leakRateMillis;
            if (leakedDrops > 0) {
                waterLevel = Math.max(0, waterLevel - (int) leakedDrops);
                lastLeakTime = now;
            }
        }
    }

    @Override
    public boolean allowRequest(String userId) {
        userBuckets.putIfAbsent(userId, new Bucket());
        return userBuckets.get(userId).allowRequest();
    }
}