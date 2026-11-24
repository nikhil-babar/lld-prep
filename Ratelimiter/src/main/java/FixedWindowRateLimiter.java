import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FixedWindowRateLimiter implements RateLimiter {

    FixedWindowRateLimiter(RateLimiterConfig config, Clock clock) {
        this.config = config;
        this.clock = clock;
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(this::clean, 1, 2, TimeUnit.SECONDS);
    }

    private record UserRef(String userId, long windowId) {}

    private final Map<UserRef, Long> windowMap = new ConcurrentHashMap<>();
    private final RateLimiterConfig config;
    private final Clock clock;

    private long currentWindow(){
        long currentTimeMs = clock.millis();
        return clock.millis() / config.getWindowMs();
    }

    @Override
    public boolean allow(String userId) {
        long currentWindow = currentWindow();
        UserRef key = new UserRef(userId, currentWindow);
        boolean[] ans = new boolean[1];
        windowMap.compute(key, (k, v) -> {
            if(v == null){
                ans[0] = true;
                return 1L;
            }
            if(v >= config.getMaxRequestPerWindow()){
                return v;
            }
            ans[0] = true;
            return v + 1;
        });
        return ans[0];
    }

    private void clean(){
        long currentWindow = currentWindow();
        for(UserRef key : windowMap.keySet()){
            if(key.windowId < currentWindow){
                windowMap.remove(key);
            }
        }
    }
}
