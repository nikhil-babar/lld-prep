package Interfaces;

import Cache.CacheKey;

import java.util.function.Consumer;

public interface IExpiryTracker {
     void setKeyExpiry(CacheKey key, long expiryAt);

     void setExpiryHandler(Consumer<CacheKey> expiryConsumer);

     void removeKey(CacheKey key) throws Exception;

     public boolean isExpired(CacheKey key) throws Exception;
}
