package Interfaces;

import Cache.CacheKey;
import Cache.CacheValue;

import java.util.Optional;

public interface ICacheStore {
    Optional<CacheValue> get(CacheKey key);

    void put(CacheKey key, CacheValue value) throws Exception;

    void incr(CacheKey key) throws Exception;

    void decr(CacheKey key) throws Exception;

    void remove(CacheKey key);

    void flush(CacheKey key);

    boolean isFull();

    boolean isEmpty();
}
