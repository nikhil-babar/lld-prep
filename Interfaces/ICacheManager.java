package Interfaces;

import java.util.Optional;

public interface ICacheManager {
    <K, V> Optional<V> get(K key);

    <K, V> void put(K key, V value);

    <K, V> void incr(K key, V value);

    <K, V> void decr(K key, V value);

    <K> void remove(K key);

    <K> void flush(K key);
}
