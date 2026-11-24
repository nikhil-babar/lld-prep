package Provider;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantRoomLockProvider implements RoomLockProvider {

    private record Ref (String roomId, LocalDate date) {}
    private static class Lock {
        private final ReentrantLock lock;
        private int count;

        public Lock(ReentrantLock lock) {
            this.lock = lock;
        }
    }
    private final Map<Ref, ReentrantLock> locks = new ConcurrentHashMap<>();

    public void withLock(String roomId, LocalDate date, Runnable action) {
        Ref ref = new Ref(roomId, date);
        Lock lockRef;
        synchronized (locks){
            lockRef = locks.computeIfAbsent(ref, (r) -> new Lock(new ReentrantLock(), 0));
            lockRef.count++;
        }
        lockRef.lock.lock();
        try
        {
            action.run();
        } finally {
            lockRef.lock.unlock();
            if(lockRef.count == 0){
                locks.remove(ref);
            }
        }
    }
}
