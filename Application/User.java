package Application;

public class User {
    public long getUserId() {
        return userId;
    }

    public User setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User(long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    private long userId;
    private String name;
}
