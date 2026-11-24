package taskmgmt;

import java.time.LocalDate;
import java.util.Set;

/** Partial update: a null component means "leave unchanged". */
public record TaskUpdate(String title, String description, Priority priority,
                         LocalDate dueDate, Set<String> labels) {}
