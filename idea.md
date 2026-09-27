
Caching.Cache
- Get, Put, Remove operation
- TTL per key and variable
- Eviction Policy
- Automatic Cleanup of keys on expiry
- INCR and DECR keys


Process

- While putting keys, user will set ttl
- If while putting the memory is full, then remove a key from the store
- Clean the values in the background
- PER user consistency (read your writes) - Already covered
- Segmentation if possible



Entities

- Datastore (key, value store)
- Eviction policy
- Caching.Cache (datastore, eviction_policy) (One segment of cache)
- CacheManager (holds segments and eviction_policy) - orchestration
- Caching.ExpiryTracker (Background thread)


Concurrency

- Updates on eviction datastructure
- Updates on datastore
- INCR and DECR updates
