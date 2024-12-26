package org.example;

import org.example.services.RateLimitService;


public class App 
{
    public static void main(String[] args) {
        RateLimitService rateLimitService = RateLimitService.getInstance();
        String pivotId = "testPivotId";

        for (int i = 0; i < 15; i++) {
            boolean allowed = rateLimitService.allowRequest(pivotId);
            System.out.println("Request " + (i + 1) + " allowed: " + allowed);
        }
    }
}
