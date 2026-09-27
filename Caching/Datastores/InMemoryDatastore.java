package Caching.Datastores;

import Caching.Cache.CacheKey;
import Caching.Cache.CacheValue;
import Caching.Interfaces.ICacheStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryDatastore implements ICacheStore {

    private final Map<CacheKey, CacheValue> map;
    private final long MAX_KEYS = 5;

    public InMemoryDatastore() {
        this.map = new HashMap<>();
    }

    @Override
    public Optional<CacheValue> get(CacheKey key) {
        CacheValue value = map.getOrDefault(key, null);
        if (value == null) return Optional.empty();
        return Optional.of(value);
    }

    @Override
    public void put(CacheKey key, CacheValue value) throws Exception {
        if(map.size() >= MAX_KEYS) throw new Exception("");
        map.put(key, value);
    }

    @Override
    public void incr(CacheKey key) throws Exception {
        if(map.get(key).isNumeric()) throw new Exception("");
        map.computeIfPresent(key,
                (_, currValue) -> {
                    try {
                        return handleChange(currValue, 1L);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void decr(CacheKey key) throws Exception {
        if(map.get(key).isNumeric()) throw new Exception("");
        map.computeIfPresent(key,
                (_, currValue) -> {
                    try {
                        return handleChange(currValue, -1L);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void remove(CacheKey key) {
        map.remove(key);
    }

    @Override
    public void flush(CacheKey key) {
        map.clear();
    }

    @Override
    public boolean isFull() {
        return map.size() >= MAX_KEYS;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }






    private CacheValue handleChange(CacheValue value, Long factor) throws Exception {
        if(!value.isNumeric()){
            throw new Exception("");
        }
        return new CacheValue(value.getValue(Long.class) + factor);
    }
}
