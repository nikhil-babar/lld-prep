package Caching.Application;

import Caching.Cache.CacheKey;
import Caching.Interfaces.IKeySerializer;

public class UserCacheKeySerializer implements IKeySerializer<User> {

    @Override
    public User deserialize(CacheKey cacheKey) throws Exception {
        String[] userDetails = cacheKey.getKey().split("_");
        if(userDetails.length != 2) throw new Exception();
        return new User(Long.parseLong(userDetails[0]), userDetails[1]);
    }

    @Override
    public CacheKey serialize(User user) {
        return new CacheKey(user.getUserId() + "_" + user.getName());
    }
}
