package Caching.ExpiryTracker;

import Caching.Cache.CacheKey;
import Caching.Datastructures.DoublyLinkedList;
import Caching.Datastructures.DoublyLinkedListNode;
import Caching.Interfaces.IExpiryTracker;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TimeWheelExpiryTracker implements IExpiryTracker, AutoCloseable {

    private Consumer<CacheKey> consumer;
    private final int slots;

    private final int ticker;
    private final List<DoublyLinkedList<ExpiryItem>> bucket;
    private final Map<CacheKey, ExpiryItem> slotMap;

    private final AtomicInteger currentSlot;
    private final ScheduledExecutorService expiryTracker;

    public TimeWheelExpiryTracker(int slots, int ticker) {
        this.slots = slots;
        this.ticker = ticker;
        this.bucket = new ArrayList<DoublyLinkedList<ExpiryItem>>(slots);
        this.slotMap = new HashMap<>();

        for(int i = 0 ; i < slots ; i++) this.bucket.add(new DoublyLinkedList<>());

        this.currentSlot = new AtomicInteger(0);
        this.expiryTracker = Executors.newSingleThreadScheduledExecutor();
        this.expiryTracker.scheduleAtFixedRate(this::removeExpiredKeys, 0, ticker, TimeUnit.MILLISECONDS);
    }

    public boolean isExpired(CacheKey key){
        if(!slotMap.containsKey(key)) {
            return false;
        }
        return slotMap.get(key).getExpiryAt() < System.currentTimeMillis();
    }

    @Override
    public void setKeyExpiry(CacheKey key, long expiryAt) {
        if(slotMap.containsKey(key)){
            try {
                removeKey(key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        int destination = destinationSlot(expiryAt);
        ExpiryItem item = new ExpiryItem(key, roundsRequired(expiryAt), destination, expiryAt);

        bucket.get(destination).addToHead(new DoublyLinkedListNode<>(item));
        slotMap.put(key, item);
    }

    @Override
    public void setExpiryHandler(Consumer<CacheKey> expiryConsumer) {
        this.consumer = expiryConsumer;
    }

    @Override
    public void removeKey(CacheKey key) throws Exception {
        if(!slotMap.containsKey(key)){
            return;
        }
        ExpiryItem expiryItem = slotMap.get(key);
        if(!bucket.get(expiryItem.getSlot()).findAndRemove(expiryItem)){
            throw new Exception("");
        }
    }

    private int destinationSlot(long expiryAt){
        return (int)((currentSlot.get() + (long)Math.ceil((expiryAt * 1.0) / ticker)) % slots);
    }

    private long roundsRequired(long expiryAt){
        return (expiryAt / ticker);
    }

    private void removeExpiredKeys() {
        DoublyLinkedList<ExpiryItem> currentBucket = bucket.get(currentSlot.get());
        DoublyLinkedList<ExpiryItem> updatedBucket = new DoublyLinkedList<>();

        for (Iterator<ExpiryItem> it = currentBucket.getIterator(); it.hasNext(); ) {
            ExpiryItem item = it.next();
            if(item.getRoundsRequired() == 0){
                consumer.accept(item.getCacheKey());
            } else {
                ExpiryItem updatedItem = new ExpiryItem(item.getCacheKey(), item.getRoundsRequired()-1, item.getSlot(), item.getExpiryAt());
                updatedBucket.addToHead(new DoublyLinkedListNode<>(updatedItem));
            }
        }

        bucket.set(currentSlot.get(), updatedBucket);
        currentSlot.getAndUpdate(slot -> (slot + 1) % this.slots);
    }

    @Override
    public void close() throws Exception {
        this.expiryTracker.close();
    }
}
