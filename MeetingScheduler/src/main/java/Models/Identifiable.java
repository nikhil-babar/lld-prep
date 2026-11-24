package Models;

import java.util.UUID;

public class Identifiable {
    private final String id;

    public Identifiable() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}
