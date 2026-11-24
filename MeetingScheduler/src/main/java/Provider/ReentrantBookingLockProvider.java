package Provider;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ReentrantBookingLockProvider implements BookingLockProvider {

    private record Ref (String roomId, LocalDate date) {}
    private static class Lock {
        private final ReentrantLock lock;
        private int count;
        public Lock(ReentrantLock lock) {
            this.lock = lock;
        }
    }
    private final Map<Ref, Lock> locks = new ConcurrentHashMap<>();

    @Override
    public <T> T withLock(String roomId, LocalDate date, Supplier<T> action) {
        Ref ref = new Ref(roomId, date);
        Lock lockRef;
        synchronized (locks){
            lockRef = locks.computeIfAbsent(ref, (r) -> new Lock(new ReentrantLock()));
            lockRef.count++;
        }
        lockRef.lock.lock();
        try
        {
            return action.get();
        } finally {
            lockRef.lock.unlock();
            if(lockRef.count == 0){
                locks.remove(ref);
            }
        }
    }
}
