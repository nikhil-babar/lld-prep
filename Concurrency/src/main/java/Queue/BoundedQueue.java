package Queue;
import java.time.Duration;

public interface BoundedQueue<T> {
    void put(T item, Duration timeout) throws InterruptedException;
    T poll(Duration timeout) throws InterruptedException;
    boolean isEmpty(Duration timeout) throws InterruptedException;
}
