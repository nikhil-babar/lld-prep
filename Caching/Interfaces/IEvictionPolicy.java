package Caching.Interfaces;

import Caching.Cache.CacheKey;

import java.util.Optional;

public interface IEvictionPolicy {
    void keyAccessed(CacheKey key);

    void evict() throws Exception;

    Optional<CacheKey> evictionCandidate();
}
