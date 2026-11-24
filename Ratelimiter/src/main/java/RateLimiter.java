public interface RateLimiter {
    boolean allow(String userId);
}
