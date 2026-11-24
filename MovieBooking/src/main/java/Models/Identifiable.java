package Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Identifiable {
    private final String id = UUID.randomUUID().toString();
}
