package Models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class TaskMetadata {
    @NonNull  private final String title;
    @NonNull private final String description;
    @NonNull private final List<String> labels;
    private final LocalDateTime dueDate;
    private final User assignee;
}
