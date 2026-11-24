package taskmgmt;

import java.util.HashMap;
import java.util.Map;

public class UserRegistry {
    private final Map<String, User> users = new HashMap<>();

    public synchronized User register(String id, String name) {
        if (id == null || id.isBlank() || name == null || name.isBlank())
            throw new TaskException("User id and name are required");
        if (users.containsKey(id)) throw new TaskException("User already exists: " + id);
        User user = new User(id, name);
        users.put(id, user);
        return user;
    }

    public synchronized User get(String id) {
        User user = users.get(id);
        if (user == null) throw new TaskException("User not found: " + id);
        return user;
    }
}
