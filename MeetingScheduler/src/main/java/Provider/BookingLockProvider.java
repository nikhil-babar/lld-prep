package Provider;

import java.time.LocalDate;

public interface RoomLockProvider {
    void withLock(String roomId, LocalDate date, Runnable action);
}
