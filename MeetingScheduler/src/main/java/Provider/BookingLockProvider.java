package Provider;

import java.time.LocalDate;
import java.util.function.Supplier;

public interface BookingLockProvider {
    <T> T withLock(String roomId, LocalDate date, Supplier<T> action);
}
