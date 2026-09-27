package Caching;

import Caching.Application.Movie;
import Caching.Application.User;
import Caching.Application.UserCacheKeySerializer;
import Caching.Cache.Cache;
import Caching.Datastores.InMemoryDatastore;
import Caching.EvictionPolicies.LRUEvictionPolicy;
import Caching.ExpiryTracker.TimeWheelExpiryTracker;

public class Main {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        try {
            testBasicEviction();
            testAccessUpdatesRecency();
            testPutOnExistingKeyUpdatesRecency();
            testMultipleEvictionsInOrder();
            testExpiry();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\n---- SUMMARY ----");
        System.out.println("Passed: " + passed + ", Failed: " + failed);
    }

    static Cache<User, Movie> newCache() throws Exception {
        return new Cache.Builder<User, Movie>()
                .setValueType(Movie.class)
                .setDataStore(new InMemoryDatastore())
                .setEvictionPolicy(new LRUEvictionPolicy())
                .setExpiryTracker(new TimeWheelExpiryTracker(16, 100))
                .setKeySerializer(new UserCacheKeySerializer())
                .build();
    }

    static void testBasicEviction() throws Exception {
        System.out.println("\n[Test] Basic LRU eviction");
        Cache<User, Movie> cache = newCache();

        User u1 = new User(1, "User1");
        User u2 = new User(2, "User2");
        User u3 = new User(3, "User3");
        User u4 = new User(4, "User4");
        User u5 = new User(5, "User5");
        User u6 = new User(6, "User6");

        cache.put(u1, new Movie("Interstellar", 10));
        cache.put(u2, new Movie("Inception", 9));
        cache.put(u3, new Movie("Tenet", 8));
        cache.put(u4, new Movie("Dunkirk", 9));
        cache.put(u5, new Movie("Oppenheimer", 10));

        cache.put(u6, new Movie("The Prestige", 9)); // should evict u1

        check("u1 evicted", cache.get(u1).isEmpty());
        check("u2 present", cache.get(u2).isPresent());
        check("u3 present", cache.get(u3).isPresent());
        check("u4 present", cache.get(u4).isPresent());
        check("u5 present", cache.get(u5).isPresent());
        check("u6 present", cache.get(u6).isPresent());
    }

    static void testAccessUpdatesRecency() throws Exception {
        System.out.println("\n[Test] Access via get() updates recency");
        Cache<User, Movie> cache = newCache();

        User u1 = new User(1, "User1");
        User u2 = new User(2, "User2");
        User u3 = new User(3, "User3");
        User u4 = new User(4, "User4");
        User u5 = new User(5, "User5");
        User u6 = new User(6, "User6");

        cache.put(u1, new Movie("Interstellar", 10));
        cache.put(u2, new Movie("Inception", 9));
        cache.put(u3, new Movie("Tenet", 8));
        cache.put(u4, new Movie("Dunkirk", 9));
        cache.put(u5, new Movie("Oppenheimer", 10));

        cache.get(u1); // touch u1, now u2 is LRU

        cache.put(u6, new Movie("The Prestige", 9)); // should evict u2

        check("u2 evicted", cache.get(u2).isEmpty());
        check("u1 survived (was touched)", cache.get(u1).isPresent());
        check("u6 present", cache.get(u6).isPresent());
    }

    static void testPutOnExistingKeyUpdatesRecency() throws Exception {
        System.out.println("\n[Test] Re-put on existing key refreshes recency");
        Cache<User, Movie> cache = newCache();

        User u1 = new User(1, "User1");
        User u2 = new User(2, "User2");
        User u3 = new User(3, "User3");
        User u4 = new User(4, "User4");
        User u5 = new User(5, "User5");
        User u6 = new User(6, "User6");

        cache.put(u1, new Movie("Interstellar", 10));
        cache.put(u2, new Movie("Inception", 9));
        cache.put(u3, new Movie("Tenet", 8));
        cache.put(u4, new Movie("Dunkirk", 9));
        cache.put(u5, new Movie("Oppenheimer", 10));

        cache.put(u1, new Movie("Interstellar (Extended)", 10)); // refresh u1

        cache.put(u6, new Movie("The Prestige", 9)); // should evict u2, not u1

        check("u2 evicted", cache.get(u2).isEmpty());
        check("u1 survived (was refreshed)", cache.get(u1).isPresent());
    }

    static void testMultipleEvictionsInOrder() throws Exception {
        System.out.println("\n[Test] Multiple sequential evictions follow LRU order");
        Cache<User, Movie> cache = newCache();

        User u1 = new User(1, "User1");
        User u2 = new User(2, "User2");
        User u3 = new User(3, "User3");
        User u4 = new User(4, "User4");
        User u5 = new User(5, "User5");
        User u6 = new User(6, "User6");
        User u7 = new User(7, "User7");
        User u8 = new User(8, "User8");

        cache.put(u1, new Movie("M1", 5));
        cache.put(u2, new Movie("M2", 5));
        cache.put(u3, new Movie("M3", 5));
        cache.put(u4, new Movie("M4", 5));
        cache.put(u5, new Movie("M5", 5));

        cache.put(u6, new Movie("M6", 5)); // evicts u1
        cache.put(u7, new Movie("M7", 5)); // evicts u2
        cache.put(u8, new Movie("M8", 5)); // evicts u3

        check("u1 evicted", cache.get(u1).isEmpty());
        check("u2 evicted", cache.get(u2).isEmpty());
        check("u3 evicted", cache.get(u3).isEmpty());

        check("u4 present", cache.get(u4).isPresent());
        check("u5 present", cache.get(u5).isPresent());
        check("u6 present", cache.get(u6).isPresent());
        check("u7 present", cache.get(u7).isPresent());
        check("u8 present", cache.get(u8).isPresent());
    }

    static void testExpiry() throws Exception {
        System.out.println("\n[Test] TTL-based expiry (TimeWheel)");
        Cache<User, Movie> cache = newCache();

        User u1 = new User(1, "User1"); // short TTL, should expire
        User u2 = new User(2, "User2"); // long TTL, should survive the first check
        User u3 = new User(3, "User3"); // no explicit TTL (default), should survive

        long now = System.currentTimeMillis();

        // Short-lived entry: expires in ~100ms (1 tick)
        cache.put(u1, new Movie("Interstellar", 10), now + 1000);

        // Longer-lived entry: expires in ~400ms (4 ticks)
        cache.put(u2, new Movie("Inception", 9), now + 3000);

        // No expiry passed -> should not expire on its own
        cache.put(u3, new Movie("Tenet", 8));

        // Wait past u1's expiry, with slack for tick granularity + scheduling jitter
        Thread.sleep(1000 + 20);

        check("u1 expired after short TTL", cache.get(u1).isEmpty());
        check("u2 still present before its TTL elapses", cache.get(u2).isPresent());
        check("u3 still present (no TTL set)", cache.get(u3).isPresent());

        // Wait past u2's expiry as well, again with slack
        Thread.sleep(3000 + 20);

        check("u2 expired after longer TTL", cache.get(u2).isEmpty());
        check("u3 still present (no TTL set)", cache.get(u3).isPresent());
    }

    static void check(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS: " + label);
        } else {
            failed++;
            System.out.println("  FAIL: " + label);
        }
    }
}