
/*
    Fixed Window Rate Limiter
    Sliding Window Rate Limiter
    Token Bucket
    Leaky Bucket
 */


import java.time.Instant;
import java.util.concurrent.*;


public class Main {

    private static boolean allow(String user, Instant at, MutableClock clock, RateLimiter limiter){
        clock.advance(at.toEpochMilli() - clock.millis());
        return limiter.allow(user);
    }

    public static void main(String[] args) throws InterruptedException {
        Instant start = Instant.ofEpochMilli(0);
        MutableClock clock = new MutableClock(start);
        RateLimiterConfig config = new RateLimiterConfig(3, 300);
        RateLimiter limiter = new SlidingWindowRateLimiter(config, clock);

        if(!allow("u1", start.plusMillis(100), clock, limiter)){
            throw new AssertionError("U1 should have been accepted at 100ms");
        }

        if(!allow("u1", start.plusMillis(200), clock, limiter)){
            throw new AssertionError("U1 should have been accepted at 200ms");
        }

        if(!allow("u1", start.plusMillis(300), clock, limiter)){
            throw new AssertionError("U1 should have been accepted at 300ms");
        }

        if(!allow("u1", start.plusMillis(400), clock, limiter)){
            throw new AssertionError("U1 should have been accepted at 400ms");
        }
    }
}
