package Models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Builder
@ToString
@Getter
public class Group {

    @Builder.Default
    @EqualsAndHashCode.Include
    private final String id = UUID.randomUUID().toString();

    private String name;
    private List<User> participants;
}
