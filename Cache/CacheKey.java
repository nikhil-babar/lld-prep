package Cache;

import java.util.Objects;

public class CacheKey {
    private final String key;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CacheKey cacheKey)) return false;
        return Objects.equals(key, cacheKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    public CacheKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
