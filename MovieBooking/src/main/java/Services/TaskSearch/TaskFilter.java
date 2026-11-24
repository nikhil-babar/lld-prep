package Services.TaskSearch;

import Models.Task;
import Models.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class TaskFilter {
    private final User byAssignee;
    private final User byReporter;
    private final boolean byAheadOfDueDate;
    private final String byLabel;
}
