import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SlidingWindowRateLimiter implements RateLimiter {

    private final RateLimiterConfig config;
    private final Clock clock;
    private final Map<String, ArrayDeque<Long>> userRequests = new ConcurrentHashMap<>();

    SlidingWindowRateLimiter(RateLimiterConfig config, Clock clock) {
        this.config = config;
        this.clock = clock;
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "sliding_window_rate_limiter_cleanup");
            thread.setDaemon(true   );
            return thread;
        });
        service.scheduleAtFixedRate(this::clean, 1, 2, TimeUnit.SECONDS);
    }

    @Override
    public boolean allow(String userId) {
        boolean[] ans = new boolean[1];
        userRequests.compute(userId, (k, v) -> {
            long currentTime = clock.millis();
            if(v == null){
                ans[0] = true;
                ArrayDeque<Long> deque = new ArrayDeque<>();
                deque.add(currentTime);
                return deque;
            }
            while(!v.isEmpty() && v.peekFirst() <= currentTime - config.getWindowMs()){
                v.pollFirst();
            }
            if(v.size() >= config.getMaxRequestPerWindow()){
                return v;
            }
            v.addLast(currentTime);
            ans[0] = true;
            return v;
        });
        return ans[0];
    }

    private void clean(){
        long currentTime = clock.millis();
        for(String key : userRequests.keySet()){
            userRequests.computeIfPresent(key, (k, v) -> {
                while(!v.isEmpty() && v.peekFirst() < currentTime - config.getWindowMs()){
                    v.pollFirst();
                }
                return v.isEmpty() ? null : v;
            });
        }
    }
}
