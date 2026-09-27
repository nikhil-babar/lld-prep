package Caching.Interfaces;

import java.util.Optional;

public interface ICache<K, V>  {
    Optional<V> get(K key);

     void put(K key, V value) throws Exception;

    void put(K key, V value, long expiryAt) throws Exception;

    void incr(K key, V value) throws Exception;

     void decr(K key, V value) throws Exception;

    void remove(K key);

    void flush(K key);
}
