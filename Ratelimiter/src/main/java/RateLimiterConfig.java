public class RateLimiterConfig {
    public RateLimiterConfig(long maxRequestPerWindow, long windowMs) {
        this.maxRequestPerWindow = maxRequestPerWindow;
        this.windowMs = windowMs;
    }

    public long getMaxRequestPerWindow() {
        return maxRequestPerWindow;
    }

    public long getWindowMs() {
        return windowMs;
    }

    private final long maxRequestPerWindow;
    private final long windowMs;
}
