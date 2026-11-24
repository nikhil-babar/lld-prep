package Models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@ToString
public class User extends Identifiable {
    @ToString.Include
    @NonNull private final String name;
    @NonNull private final String email;
}
