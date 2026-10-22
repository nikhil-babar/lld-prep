package Queue;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConcurrentBoundedQueue<T> implements BoundedQueue<T> {
    private final List<T> items;
    private int head;
    private int tail;
    private final int capacity;
    private final ReentrantLock lock;
    private final Condition notFull, notEmpty;

    public ConcurrentBoundedQueue(int capacity, Supplier<T> supplier){
        items = Stream.generate(supplier).limit(capacity).collect(Collectors.toList());
        head = -1;
        tail = -1;
        this.capacity = capacity;
        this.lock = new ReentrantLock(false);
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
    }

    @Override
    public void put(T item, Duration timeout) throws InterruptedException {
        try
        {
            acquireLock(timeout);
            while(isFull()) notFull.await();
            put(item);
            notEmpty.signal();
        }  finally {
            lock.unlock();
        }
    }

    public boolean isEmpty(Duration timeout) throws InterruptedException {
        try
        {
            acquireLock(timeout);
            return isEmpty();
        }  finally {
            lock.unlock();
        }
    }

    private void put(T item){
        head = (head + 1) % capacity;
        items.set(head, item);
        if(tail == -1){
            tail = head;
        }
    }

    private void acquireLock(Duration timeout) throws InterruptedException {
        if(!lock.tryLock(timeout.getNano(), TimeUnit.NANOSECONDS)){
            throw new InterruptedException("Couldn't acquire lock on queue");
        }
    }

    private T poll(){
        T item = items.get(tail);
        if(head == tail) tail = -1;
        else tail = (tail + 1) % capacity;
        return item;
    }

    private boolean isFull(){
        return ((head + 1) % capacity == tail);
    }

    private boolean isEmpty(){
        return tail == -1;
    }

    @Override
    public T poll(Duration timeout) throws InterruptedException {
        try
        {
            acquireLock(timeout);
            while(isEmpty()) notEmpty.await();
            T item = poll();
            notFull.signal();
            return item;
        }  finally {
            lock.unlock();
        }
    }
}
