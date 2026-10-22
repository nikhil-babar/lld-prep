import Queue.BoundedQueue;
import Queue.ConcurrentBoundedQueue;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            BoundedQueue<Integer> q = new ConcurrentBoundedQueue<>(3000, new Supplier<Integer>() {
                @Override
                public Integer get() {
                    return 0;
                }
            });

            Duration duration = Duration.ofMillis(100);
            int producers = 3, perProducer = 1000;
            CountDownLatch start = new CountDownLatch(1);
            Thread[] threads = new Thread[producers];
            int[] failures = {0};

            for (int p = 0; p < producers; p++) {
                final int base = p * perProducer;
                threads[p] = new Thread(() -> {
                    try {
                        start.await();
                        for (int i = 0; i < perProducer; i++) {
                            q.put(base + i, duration);
                        }
                    } catch (Throwable t) {
                        synchronized (failures) { failures[0]++; }
                        System.out.println("  thread crashed: " + t);
                    }
                });
                threads[p].start();
            }

            start.countDown();           // release all producers at once
            for (Thread t : threads) t.join();

            long distinct = 0;
            Integer item;
            while (q.isEmpty(duration) && (item = q.poll(duration)) != null) {
                distinct++;
            }
            System.out.printf("Run %d: expected=3000 size=%d distinct=%d crashed=%d%n",
                    run, distinct, distinct, failures[0]);
        }
    }
}
