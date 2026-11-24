package Models;

import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

public record TaskView(
        String id,
        String title,
        String description,
        List<String> labels,
        LocalDateTime dueDate,
        User assignee
) {
}
