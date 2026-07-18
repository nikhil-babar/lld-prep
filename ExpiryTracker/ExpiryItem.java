package ExpiryTracker;

import Cache.CacheKey;

public class ExpiryItem {
    private final CacheKey cacheKey;

    public CacheKey getCacheKey() {
        return cacheKey;
    }

    public long getRoundsRequired() {
        return roundsRequired;
    }

    public int getSlot() {
        return slot;
    }

    private final long roundsRequired;
    private final int slot;
    private final long expiryAt;

    public ExpiryItem(CacheKey cacheKey, long roundsRequired, int slot, long expiryAt) {
        this.cacheKey = cacheKey;
        this.roundsRequired = roundsRequired;
        this.slot = slot;
        this.expiryAt = expiryAt;
    }

    public long getExpiryAt() {
        return expiryAt;
    }
}
