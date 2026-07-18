package Cache;

import Interfaces.*;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Cache<K, V> implements ICache<K, V> {

    private final ICacheStore dataStore;
    private final IEvictionPolicy evictionPolicy;
    private final IExpiryTracker expiryTracker;
    private final IKeySerializer<K> keySerializer;
    private final Class<V> valueType;
    private final ReentrantLock operationLock;

    public Cache(Builder<K, V> builder) {
        this.dataStore = builder.dataStore;
        this.evictionPolicy = builder.evictionPolicy;
        this.expiryTracker = builder.expiryTracker;
        this.expiryTracker.setExpiryHandler(this::remove);
        this.keySerializer = builder.keySerializer;
        this.valueType = builder.valueType;
        this.operationLock = new ReentrantLock();
    }

    private CacheKey getKey(K key){
        return keySerializer.serialize(key);
    }

    private CacheValue getValue(V value){
        return new CacheValue(value);
    }

    private <T> T withLock(Supplier<T> func){
        operationLock.lock();
        try {
            return func.get();
        } finally {
            operationLock.unlock();
        }
    }

    private void withLock(Runnable func){
        operationLock.lock();
        try {
            func.run();
        } finally {
            operationLock.unlock();
        }
    }

    @Override
    public Optional<V> get(K key) {
        return withLock(() -> {
            Optional<CacheValue> cacheValue = dataStore.get(getKey(key));
            if(cacheValue.isEmpty()) return Optional.empty();
            try {
                if(expiryTracker.isExpired(getKey(key))){
                    return Optional.empty();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            keyAccessed(key);
            V value = cacheValue.get().getValue(valueType);
            return Optional.ofNullable(value);
        });
    }

    @Override
    public void put(K key, V value) throws Exception {
        putImpl(key, value, -1L);
    }

    @Override
    public void put(K key, V value, long expiryAt) throws Exception {
        putImpl(key, value, expiryAt);
    }

    @Override
    public void incr(K key, V value) throws Exception {
        withLock(() -> {
            try
            {
                dataStore.incr(getKey(key));
            } catch (Exception e) {
                throw new RuntimeException("");
            }
            keyAccessed(key);
        });
    }

    private void keyAccessed(K key) {
        evictionPolicy.keyAccessed(getKey(key));
    }

    @Override
    public void decr(K key, V value) throws Exception {
        withLock(() -> {
            try
            {
                dataStore.decr(getKey(key));
            } catch (Exception e) {
                throw new RuntimeException("");
            }
            keyAccessed(key);
        });
    }

    @Override
    public void remove(K key) {
        remove(getKey(key));
    }

    private void remove(CacheKey key) {
        withLock(() -> {
            dataStore.remove(key);
            try {
                expiryTracker.removeKey(key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void flush(K key) {
        withLock(() -> {
            dataStore.flush(getKey(key));
        });
    }

    private void putImpl(K key, V value, long expiryAt) throws Exception {
        withLock(() -> {
            try {
                if(dataStore.isFull()){
                    Optional<CacheKey> candidate = evictionPolicy.evictionCandidate();
                    if(candidate.isEmpty()){
                        throw new Exception("No candidate");
                    }
                    dataStore.remove(candidate.get());
                    evictionPolicy.evict();
                    expiryTracker.removeKey(getKey(key));
                }

                dataStore.put(getKey(key), getValue(value));
                keyAccessed(key);

                if(expiryAt > 0)
                    expiryTracker.setKeyExpiry(getKey(key), expiryAt);
            } catch (Exception ex){
                throw new RuntimeException(ex);
            }
        });
    }

    public static class Builder <K, V> {
        public Builder<K, V> setDataStore(ICacheStore dataStore) {
            this.dataStore = dataStore;
            return this;
        }

        public Builder<K, V> setEvictionPolicy(IEvictionPolicy evictionPolicy) {
            this.evictionPolicy = evictionPolicy;
            return this;
        }

        public Builder<K, V> setExpiryTracker(IExpiryTracker expiryTracker) {
            this.expiryTracker = expiryTracker;
            return this;
        }

        public Builder<K, V> setKeySerializer(IKeySerializer<K> keySerializer) {
            this.keySerializer = keySerializer;
            return this;
        }

        public Builder<K, V> setValueType(Class<V> valueType) {
            this.valueType = valueType;
            return this;
        }

        public Cache<K, V> build() throws Exception {
            if(
                    this.dataStore != null
                    && this.keySerializer != null
                    && this.valueType != null
            ){
                return new Cache<K, V>(this);
            }
            throw new Exception("");
        }

        private ICacheStore dataStore;
        private IEvictionPolicy evictionPolicy;
        private IExpiryTracker expiryTracker;
        private IKeySerializer<K> keySerializer;
        private Class<V> valueType;
    }
}
