package Models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
public class TaskData {
    @NonNull  private String title;
    @NonNull private String description;
    @NonNull private List<String> labels;
    private LocalDateTime dueDate;
}
