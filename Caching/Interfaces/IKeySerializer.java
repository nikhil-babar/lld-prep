package Caching.Interfaces;

import Caching.Cache.CacheKey;

public interface IKeySerializer<K> {
    K deserialize(CacheKey cacheKey) throws Exception;

    CacheKey serialize(K key);
}
