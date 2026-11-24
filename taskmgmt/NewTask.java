package taskmgmt;

import java.time.LocalDate;
import java.util.Set;

/** Input for createTask. priority defaults to MEDIUM, dueDate/labels/assigneeId are optional. */
public record NewTask(String title, String description, Priority priority,
                      LocalDate dueDate, Set<String> labels, String assigneeId) {}
